package com.basic.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/10.
 */
public class TimeServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup pgroup=new NioEventLoopGroup();  //一个是用来处理服务器端接受客户端的线程
        EventLoopGroup cgroup=new NioEventLoopGroup();  //一个是进行网络通信的（网络读写）
        ServerBootstrap bootstrap=new ServerBootstrap(); //2.创建辅助工具类，用于服务器通道的一系列配置
        bootstrap.group(pgroup,cgroup); //绑定两个线程组
        bootstrap.channel(NioServerSocketChannel.class);// 指定NIO模式
        bootstrap.option(ChannelOption.SO_BACKLOG,1024);//设置tcp缓冲区
        bootstrap.option(ChannelOption.SO_SNDBUF,32*1024);//设置发送缓冲区大小
        bootstrap.option(ChannelOption.SO_RCVBUF,32*1024);//设置发送缓冲区大小
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);//保持连接

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TimeServerHandler());//3.在这里配置具体的数据接受方法
            }
        });

        ChannelFuture future = bootstrap.bind(1234).sync();//进行绑定
        future.channel().closeFuture().sync();//等待关闭

    }
}
