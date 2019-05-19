package com.basic.netty.pojosolution.model;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * locate com.basic.netty.pojosolution.model
 * Created by mastertj on 2018/4/10.
 */
public class User implements Serializable{
    private int id;
    private String name;
    private int age;

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public User() {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ByteBuf encode(){
        ByteBuffer byteBuffer=ByteBuffer.allocate(name.getBytes().length+12);
        byteBuffer.putInt(name.getBytes().length);
        byteBuffer.put(name.getBytes());
        byteBuffer.putInt(id);
        byteBuffer.putInt(age);
        byteBuffer.flip();
        return Unpooled.copiedBuffer(byteBuffer);
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
