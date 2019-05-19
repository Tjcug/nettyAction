package com.basic.netty.thirdSolution;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/10.
 *  对于ChannelOption.SO_BACKLOG的解释
 *  服务器端TCP内核模块维护两个队列，一个是A一个是B
 *  客户端向服务器端connect的时候，会发送带有SYN标志的包(第一次握手)
 *  服务器端收到客户端发来的SYN包时，向客户端发送SYN ACK确认（第二次握手）
 *  此时TCP内核模块把客户端连接加入到A队列中，然后服务器收到客户端发来的ACK（第三次握手）
 *  TCP内核模块把客户端连接从A队列到B队列，连接完成，应用程序的accept返回
 *  也就是说accpet从B队列中取出完成三次握手的连接
 *  A队列和B队列的长度之和就是backlog，当A，B队列的长度之和大于backlog的时候，新连接将会被TCP内核拒绝
 *  所以，如果backlog过小，可能accept的速度更不上，A，B队列满了，导致新的客户端无法连接
 *  要注意：backlog对程序支持的连接并不影响，backlong影响的只是还没有被accpet取出的连接
 */
public class Server {
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
                //添加分隔符
                ch.pipeline().addLast(new FixedLengthFrameDecoder(14));//3.在这里配置具体的数据接受方法
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new ServerHandler());
            }
        });

        ChannelFuture future = bootstrap.bind(1234).sync();//进行绑定
        future.channel().closeFuture().sync();//等待关闭
    }
}
