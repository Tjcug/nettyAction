package com.basic.netty;

import com.basic.netty.model.RequetsInfo;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * locate com.basic.netty.firstSolution
 * Created by mastertj on 2018/4/10.
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter{
    private static HashMap<String,String> AUTH_IP_MAP=new HashMap<>();

    private static final String SUCCESS_KEY="auth_success_key";

    static {
        AUTH_IP_MAP.put("192.168.99.1","1234");
    }
    //管道被激活监听事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------active-------");
    }

    private boolean auth(ChannelHandlerContext ctx,Object msg){
        String ret[]=((String)msg).split(",");
        String key = AUTH_IP_MAP.get(ret[0]);
        if(key!=null && key.equals(ret[1])){
            ctx.writeAndFlush(SUCCESS_KEY);
            return true;
        }else {
            ctx.writeAndFlush("auth Failure").addListener(ChannelFutureListener.CLOSE);
            return false;
        }
    }

    //管道开始读取数据监听事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("---------read-------");
        if(msg instanceof String){
            auth(ctx, msg);
        }else if(msg instanceof RequetsInfo){
            RequetsInfo requetsInfo= (RequetsInfo) msg;
            System.out.println("--------------------------------------------------");
            System.out.println("当前主机ip："+requetsInfo.getIp());
            System.out.println("当前主机CPU情况: ");
            System.out.println(requetsInfo.getCupPercMap());
            System.out.println("当前主机Mem情况: ");
            System.out.println(requetsInfo.getMemoryMap());
        }else {
            ctx.writeAndFlush("client Failure").addListener(ChannelFutureListener.CLOSE);
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
        System.out.println("---------handler add---------");
    }

    //Handler被移除
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------handler remove---------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
