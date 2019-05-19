package com.basic.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * locate com.basic.netty.http
 * Created by mastertj on 2018/4/11.
 */
public class HttpHelloWorldServerHandler extends ChannelHandlerAdapter{
    //管道被激活监听事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------active-------");
    }

    //管道开始读取数据监听事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("---------read-------");
        if(msg instanceof HttpRequest){
            HttpRequest httpRequest= (HttpRequest) msg;
            if(HttpHeaderUtil.is100ContinueExpected(httpRequest)){
                ctx.writeAndFlush(new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.CONTINUE));
            }
            boolean iskeepAlive =HttpHeaderUtil.isKeepAlive(httpRequest);
            FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("Hello World!".getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            if (!iskeepAlive) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
               ctx.writeAndFlush(response);
            }
        }
    }

    //管道读取数据完毕监听事件
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("读取数据完毕");
    }

    //Handler被添加
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------handler add---------");
    }

    //Handler被移除
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------handler remove---------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
