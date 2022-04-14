package com.github.zfy.codec;

import com.github.zfy.serialize.Serializer;
import com.github.zfy.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;

    public MessageDecoder(Class<?> genericClass) {
        this.serializer = new KryoSerializer(genericClass);
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = serializer.deserialize(data);
        out.add(obj);
    }
}
