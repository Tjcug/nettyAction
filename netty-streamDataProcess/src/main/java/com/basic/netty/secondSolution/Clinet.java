package com.basic.netty.secondSolution;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * locate com.basic.netty.secondSolution
 * Created by mastertj on 2018/4/10.
 */
public class Clinet {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup cgroup=new NioEventLoopGroup();  //一个是进行网络通信的（网络读写）
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(cgroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ByteBuf byteBuf= Unpooled.copiedBuffer("$_".getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,byteBuf));//3.在这里配置具体的数据接受方法
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new com.basic.netty.secondSolution.ClientHandler());
            }
        });

        ChannelFuture future = bootstrap.connect("localhost", 1234).sync();

        System.out.println("连接完毕--------");
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server1$_".getBytes()));
        Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server2$_".getBytes()));
        Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server3$_".getBytes()));
        Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server4$_".getBytes()));
        Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server5$_".getBytes()));

        future.channel().closeFuture().sync();
    }
}
