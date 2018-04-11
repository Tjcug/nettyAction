package com.basic.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/10.
 * Netty客户端
 */
public class Client {
    private static final class SinglonClientHolder{
        private static Client client=new Client("localhost",1234);
    }

    private static Client getInstance(){
        return SinglonClientHolder.client;
    }

    private EventLoopGroup cgroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private String host;
    private int port;

    public Client(String host,int port) {
        this.host=host;
        this.port=port;
        cgroup=new NioEventLoopGroup();  //一个是进行网络通信的（网络读写）
        bootstrap=new Bootstrap();
        bootstrap.group(cgroup);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingDecoder());
                        ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder());
                        //超时Handler （当服务器与客户端在指定时间内上没有任何进行通信，则会关闭响应的通道，主要为了减少服务器资源的占用）
                        ch.pipeline().addLast(new ReadTimeoutHandler(5));
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
    }

    public void connect(String host,int port){
        try {
            channelFuture = bootstrap.connect(host, port).sync();
            System.out.println("服务器来连接成功 host:"+host+" port: "+port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture getChannelFuture() {
        if(this.channelFuture==null){
            this.connect(host,port);
        }
        if(!this.channelFuture.channel().isActive()){
            this.connect(host,port);
        }
        return channelFuture;
}

    public static void main(String[] args) throws InterruptedException {
        Client client=Client.getInstance();
        ChannelFuture channelFuture = client.getChannelFuture();

        System.out.println("连接完毕--------");
        for(int i=0;i<5;i++){
            Request request=new Request(i,"tanjie","Hello Server  ");
            channelFuture.channel().writeAndFlush(request);
//            TimeUnit.SECONDS.sleep(4);
        }

        channelFuture.channel().closeFuture().sync();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("进入子线程");
                ChannelFuture channelFuture = client.getChannelFuture();
                System.out.println("actived :"+channelFuture.channel().isActive());
                System.out.println("oepn :"+channelFuture.channel().isOpen());
                Request request=new Request(1,"tanjie","再次发送消息 ");
                channelFuture.channel().writeAndFlush(request);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("断开连接主线程结束.....");
    }
}
