package com.github.zfy.codec;

import com.github.zfy.serialize.Serializer;
import com.github.zfy.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;


/**
 * 编码类，将object序列化为byte数组
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final Serializer serializer;

    public MessageEncoder(Class<?> genericClass) {
        this.serializer = new KryoSerializer(genericClass);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) throws Exception {
        try {
            byte[] bodyBytes = serializer.serialize(obj);
            out.writeInt(MAGIC_NUMBER);
            out.writeInt(bodyBytes.length);
            out.writeBytes(bodyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
