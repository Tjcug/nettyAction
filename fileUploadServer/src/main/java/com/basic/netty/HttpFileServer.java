package com.basic.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/11.
 */
public class HttpFileServer {
    public static final String DEFUALT_URL="/sources/";
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup pgroup=new NioEventLoopGroup();  //一个是用来处理服务器端接受客户端的线程
        EventLoopGroup cgroup=new NioEventLoopGroup();  //一个是进行网络通信的（网络读写）
        ServerBootstrap bootstrap=new ServerBootstrap(); //2.创建辅助工具类，用于服务器通道的一系列配置
        bootstrap.group(pgroup,cgroup); //绑定两个线程组
        bootstrap.channel(NioServerSocketChannel.class);// 指定NIO模式
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //加入http解码器
                ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
                //加入uObjectAggregator的解码器，作用是他会把多个消息转换为单一的FullHttpRequest或则FullHttpResponse
                ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                //加入Http编码器
                ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                //加入chunked，主要作用是支持异步发送的码流（大文件传输），但不专用过多的内存，防止java内存溢出
                ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                //加入自定义批处理文件服务器的业务逻辑handler
                ch.pipeline().addLast("fileserverHandler",new HttpFileServerHandler(DEFUALT_URL));
            }
        });

        ChannelFuture future = bootstrap.bind(1234).sync();//进行绑定

        System.out.println("Open your web browser and navigate to http://127.0.0.1:1234"+DEFUALT_URL);

        future.channel().closeFuture().sync();//等待关闭
    }
}
