package com.basic.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/10.
 * Netty客户端
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup cgroup=new NioEventLoopGroup();  //一个是进行网络通信的（网络读写）
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(cgroup);
        bootstrap.channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect("localhost", 1234).sync();
        System.out.println("连接完毕--------");
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server1".getBytes()));
        //Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server3".getBytes()));
        //Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server2".getBytes()));
        //Thread.sleep(1000);
        future.channel().writeAndFlush(Unpooled.copiedBuffer("hello! Server4".getBytes()));
        future.channel().closeFuture().sync();
    }
}
