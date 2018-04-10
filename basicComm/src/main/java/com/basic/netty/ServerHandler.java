package com.basic.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/10.
 */
public class ServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf byteBuf= (ByteBuf) msg;
            byte[] bytes=new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String reuslt=new String(bytes,"UTF-8");
            System.out.println("服务器收到消息: "+reuslt);

            //服务器给客户端响应
            String response="Hi Client";
            //Handler中有写操作 会自动释放ByteBuf对象
            ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));

//            future.addListener(ChannelFutureListener.CLOSE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerChannel 激活-----");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerChannel 读取数据完毕-----");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
