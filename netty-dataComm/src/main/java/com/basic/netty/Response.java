package com.basic.netty;

import java.io.Serializable;

/**
 * locate com.basic.netty
 * Created by mastertj on 2018/4/11.
 */
public class Response implements Serializable{
    private int id;
    private String name;
    private String responseMessage;

    public Response(int id, String name, String responseMessage) {
        this.id = id;
        this.name = name;
        this.responseMessage = responseMessage;
    }

    public Response() {
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

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
