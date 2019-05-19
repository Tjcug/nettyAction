package com.basic.netty;

import java.io.Serializable;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/11.
 */
public class Request implements Serializable{
    private int id;
    private String name;
    private String requestMessage;

    public Request(int id, String name, String requestMessage) {
        this.id = id;
        this.name = name;
        this.requestMessage = requestMessage;
    }

    public Request() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                ", requestMessage='" + requestMessage + '\'' +
                '}';
    }
}
