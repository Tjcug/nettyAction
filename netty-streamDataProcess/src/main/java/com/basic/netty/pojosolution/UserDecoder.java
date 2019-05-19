package com.basic.netty.pojosolution;

import com.basic.netty.pojosolution.model.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * locate com.basic.netty.pojosolution
 * Created by mastertj on 2018/4/10.
 */
public class UserDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<4)
            return;
        int strLen = in.readInt();
        if(in.readableBytes()<(strLen+8)){
            return;
        }
        byte[] bytes=new byte[strLen];
        in.readBytes(bytes);

        String name=new String(bytes,"utf-8");
        int id = in.readInt();
        int age = in.readInt();
        out.add(new User(id,name,age));
    }
}
