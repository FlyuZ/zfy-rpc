package com.github.zfy.codec;

import com.github.zfy.config.RpcConstants;
import com.github.zfy.dto.RpcMessage;
import com.github.zfy.serialize.Serializer;
import com.github.zfy.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;


/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder {
    private final Class<?> genericClass;
    private final Serializer serializer;

    public MessageEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
        this.serializer = new KryoSerializer(genericClass);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) throws Exception {
        try {
            // build full length
            byte[] bodyBytes = serializer.serialize(obj);
            out.writeBytes(bodyBytes);
            out.writeInt(bodyBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
