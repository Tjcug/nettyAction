package com.basic.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * locate com.basic.netty.http
 * Created by mastertj on 2018/4/11.
 */
public class HttpHelloWorldServer {
    public static final boolean SSL=System.getProperty("ssl") !=null;
    public static final int PORT=Integer.parseInt(System.getProperty("prot",SSL? "8443":"8080"));

    public static void main(String[] args) throws Exception {
        //Confugure SSL
        final SslContext sslContext;
        if(SSL){
            SelfSignedCertificate ssc=new SelfSignedCertificate();
            sslContext =SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        }else {
            sslContext=null;
        }

        EventLoopGroup pgroup=new NioEventLoopGroup();  //一个是用来处理服务器端接受客户端的线程
        EventLoopGroup cgroup=new NioEventLoopGroup();  //一个是进行网络通信的（网络读写）
        ServerBootstrap bootstrap=new ServerBootstrap(); //2.创建辅助工具类，用于服务器通道的一系列配置
        bootstrap.group(pgroup,cgroup); //绑定两个线程组
        bootstrap.channel(NioServerSocketChannel.class);// 指定NIO模式
        bootstrap.option(ChannelOption.SO_BACKLOG,1024);//设置tcp缓冲区
        bootstrap.option(ChannelOption.SO_SNDBUF,32*1024);//设置发送缓冲区大小
        bootstrap.option(ChannelOption.SO_RCVBUF,32*1024);//设置发送缓冲区大小
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childHandler(new HttpHelloWorldInitalizer(sslContext));
        ChannelFuture future = bootstrap.bind(1234).sync();//进行绑定

        System.out.println("Open your web browser and navigate to "+(SSL? "https":"http")+"://127.0.0.1:"+1234+"/");

        future.channel().closeFuture().sync();//等待关闭
    }
}
