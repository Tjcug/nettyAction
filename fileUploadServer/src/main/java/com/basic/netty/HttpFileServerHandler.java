package com.basic.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/11.
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
    private String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx,
                                   FullHttpRequest request) throws Exception {
        if(!request.decoderResult().isSuccess())
        {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if(request.method() != HttpMethod.GET)
        {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final String uri = request.uri();
        final String path = sanitizeUri(uri);
        if(path == null)
        {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        File file = new File(path);
        if(file.isHidden() || !file.exists())
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        if(file.isDirectory())
        {
            if(uri.endsWith("/"))
            {
                sendListing(ctx, file);
            }else{
                sendRedirect(ctx, uri + "/");
            }
            return;
        }
        if(!file.isFile())
        {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        //随机写文件类
        RandomAccessFile randomAccessFile = null;
        try{
            randomAccessFile = new RandomAccessFile(file, "r");
        }catch(FileNotFoundException fnfd){
            //404
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        //获取文件长度
        long fileLength = randomAccessFile.length();
        //建立响应对象
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //建议响应头
        HttpHeaderUtil.setContentLength(response, fileLength);
//        setContentLength(response, fileLength);
        setContentTypeHeader(response, file);



        if(HttpHeaderUtil.isKeepAlive(request)){
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);
        ChannelFuture sendFileFuture = null;
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {

            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
                System.out.println("Transfer complete.");

            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                                            long progress, long total) throws Exception {
                if(total < 0)
                    System.err.println("Transfer progress: " + progress);
                else
                    System.err.println("Transfer progress: " + progress + "/" + total);
            }
        });

        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!HttpHeaderUtil.isKeepAlive(request))
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive())
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private String sanitizeUri(String uri){
        try{
            //使用UTF-8编码
            uri = URLDecoder.decode(uri, "UTF-8");
        }catch(UnsupportedEncodingException e){
            try{
                //尝试ISO-8859-1
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            }catch(UnsupportedEncodingException e1){
                //抛出异常信息
                throw new Error();
            }
        }

        //对url进行细粒度判断：4步验证操作
        // step1 基础验证
        if(!uri.startsWith(url))
            return null;
        // step2 基础验证
        if(!uri.startsWith("/"))
            return null;

        // step3 将文件分隔符替换为本地操作系统的文件路径符
        uri = uri.replace('/', File.separatorChar);
        // step4 二次验证合法性
        if(uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.startsWith(".") || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        return System.getProperty("user.dir") + File.separator + uri;
    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private static void sendListing(ChannelHandlerContext ctx, File dir){
        //设置响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//        response.headers().set("CONNECT_TYPE", "text/html;charset=UTF-8");

        //响应头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

        //追加文本内容
        String dirPath = dir.getPath();
        StringBuilder buf = new StringBuilder();

        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("目录:");
        buf.append("</title></head><body>\r\n");

        buf.append("<h3>");
        buf.append(dirPath).append(" 目录：");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>链接：<a href=\" ../\")..</a></li>\r\n");
        for (File f : dir.listFiles()) {
            if(f.isHidden() || !f.canRead()) {
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }

            buf.append("<li>链接：<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }

        buf.append("</ul></body></html>\r\n");

        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private static void sendRedirect(ChannelHandlerContext ctx, String newUri){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
//        response.headers().set("LOCATIN", newUri);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        //建立响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        //设置响应头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        //使用ctx对象写出并且刷新到SocketChannel中去，并主动关闭Channel
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    private static void setContentTypeHeader(HttpResponse response, File file){
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
    }
}
