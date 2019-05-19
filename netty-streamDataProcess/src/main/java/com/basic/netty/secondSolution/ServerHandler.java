package com.basic.netty.secondSolution;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * locate com.basic.netty.firstSolution
 * Created by mastertj on 2018/4/10.
 */
public class ServerHandler extends ChannelHandlerAdapter{

    //管道被激活监听事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------active-------");
    }

    //管道开始读取数据监听事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("---------read-------");
        String request= (String) msg;
        System.out.println("Server :"+request);
        String response="我是响应数据$_";
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
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
