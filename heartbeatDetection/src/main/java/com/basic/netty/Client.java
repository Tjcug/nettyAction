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

    public Client(String host, int port) {
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
                        ch.pipeline().addLast(new ClientHeartBeatHandler());
                    }
                });
    }

    public void connect(String host,int port){
        try {
            channelFuture = bootstrap.connect(host, port).sync();
            System.out.println("服务器连接成功 host:"+host+" port: "+port);
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
        Client client = Client.getInstance();

        ChannelFuture channelFuture = client.getChannelFuture();

        channelFuture.channel().closeFuture().sync();
    }
}
