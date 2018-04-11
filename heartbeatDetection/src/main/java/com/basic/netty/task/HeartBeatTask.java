package com.basic.netty.task;

import com.basic.netty.model.RequetsInfo;
import io.netty.channel.ChannelHandlerContext;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * locate com.basic.netty.task
 * Created by mastertj on 2018/4/11.
 */
public class HeartBeatTask implements Runnable{
    private ChannelHandlerContext ctx;
    private InetAddress address;
    private Sigar sigar=new Sigar();

    public HeartBeatTask(ChannelHandlerContext ctx,InetAddress address) {
        this.ctx = ctx;
        this.address=address;
    }

    @Override
    public void run() {
        try {
            RequetsInfo requetsInfo=new RequetsInfo();
            requetsInfo.setIp(address.getHostAddress());

            //cpu prec
            CpuPerc cpuPerc = sigar.getCpuPerc();
            Map<String,Object> cpuMap=new HashMap<>();
            cpuMap.put("combined",cpuPerc.getCombined());
            cpuMap.put("user",cpuPerc.getUser());
            cpuMap.put("idle",cpuPerc.getIdle());
            cpuMap.put("sys",cpuPerc.getSys());
            cpuMap.put("wait",cpuPerc.getWait());

            Mem mem = sigar.getMem();
            Map<String,Object> memoryMap=new HashMap<>();
            memoryMap.put("total",mem.getTotal()/1024L);
            memoryMap.put("used",mem.getUsed()/1024L);
            memoryMap.put("free",mem.getFree()/1024L);

            requetsInfo.setCupPercMap(cpuMap);
            requetsInfo.setMemoryMap(memoryMap);
            ctx.writeAndFlush(requetsInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
