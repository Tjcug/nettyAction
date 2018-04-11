package com.basic.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;


/**
 * locate com.basic.netty.http
 * Created by mastertj on 2018/4/11.
 */
public class HttpHelloWorldInitalizer extends ChannelInitializer<SocketChannel> {
    private SslContext sslContext;

    public HttpHelloWorldInitalizer(SslContext sslContext) {
        this.sslContext=sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if(sslContext!=null){
            ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
        }

        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpHelloWorldServerHandler());
    }
}
