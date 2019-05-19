package com.basic.netty;

import com.basic.netty.task.HeartBeatTask;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.net.InetAddress;
import java.util.concurrent.*;

/**
 * locate com.basic.netty.firstSolution
 * Created by mastertj on 2018/4/10.
 */
public class ClientHeartBeatHandler extends ChannelHandlerAdapter{

    private ScheduledExecutorService service= Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> heartBeat;

    private InetAddress address;

    private static final String SUCCESS_KEY="auth_success_key";

    //管道被激活监听事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---------active-------");
        address=InetAddress.getLocalHost();
        String ip=address.getHostAddress();
        System.out.println("ip:"+ip);
        String key="1234";
        //证书
        String auth=ip+","+key;
        ctx.writeAndFlush(auth);
    }

    //管道开始读取数据监听事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("---------read-------");
        try {
            if(msg instanceof String){
                String result= (String) msg;
                if(result.equals(SUCCESS_KEY)){
                    //认证成功
                    service.scheduleWithFixedDelay(new HeartBeatTask(ctx,address),0,5, TimeUnit.SECONDS);
                }else {
                    System.out.println(msg);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
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
