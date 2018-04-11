package com.basic.netty.model;

import java.io.Serializable;
import java.util.Map;

/**
 * locate com.basic.netty.model
 * Created by mastertj on 2018/4/11.
 */
public class RequetsInfo implements Serializable{
    private String ip;
    private Map<String,Object> cupPercMap;
    private Map<String,Object> memoryMap;

    public RequetsInfo() {
    }

    public RequetsInfo(String ip, Map<String, Object> cupPercMap, Map<String, Object> memoryMap) {
        this.ip = ip;
        this.cupPercMap = cupPercMap;
        this.memoryMap = memoryMap;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, Object> getCupPercMap() {
        return cupPercMap;
    }

    public void setCupPercMap(Map<String, Object> cupPercMap) {
        this.cupPercMap = cupPercMap;
    }

    public Map<String, Object> getMemoryMap() {
        return memoryMap;
    }

    public void setMemoryMap(Map<String, Object> memoryMap) {
        this.memoryMap = memoryMap;
    }

    @Override
    public String toString() {
        return "RequetsInfo{" +
                "ip='" + ip + '\'' +
                ", cupPercMap=" + cupPercMap +
                ", memoryMap=" + memoryMap +
                '}';
    }
}
