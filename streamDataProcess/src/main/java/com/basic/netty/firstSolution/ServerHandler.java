package com.basic.netty.firstSolution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * locate com.basic.netty.firstSolution
 * Created by mastertj on 2018/4/10.
 */
public class ServerHandler extends ChannelHandlerAdapter{
    private ByteBuf byteBuf;

    //管道被激活监听事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------active-------");
    }

    //管道开始读取数据监听事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("---------read-------");
        ByteBuf buf= (ByteBuf) msg;
        try {
            byteBuf.writeBytes(buf);

            while (byteBuf.readableBytes()>20){
                byte[] bytes=new byte[20];
                byteBuf.readBytes(bytes);
                System.out.println(new String(bytes,"UTF-8"));
            }
        } finally {
            ReferenceCountUtil.release(buf);
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
        byteBuf=ctx.alloc().buffer(1024);
        System.out.println("---------handler add---------");
    }

    //Handler被移除
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        byteBuf.release();
        byteBuf=null;
        System.out.println("---------handler remove---------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
