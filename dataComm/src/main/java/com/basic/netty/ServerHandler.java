package com.basic.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/10.
 */
public class ServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            Request request= (Request) msg;
            System.out.println("服务器收到消息: "+request);

            //服务器给客户端响应
            Response response=new Response(1,"response","hello 我是一个响应");
            //Handler中有写操作 会自动释放ByteBuf对象
            ChannelFuture future = ctx.writeAndFlush(response);

//            future.addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
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
