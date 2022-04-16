package com.github.zfy.codec;

import com.github.zfy.serialize.Serializer;
import com.github.zfy.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 解码类，从byte数组反序列化出类
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public MessageDecoder(Class<?> genericClass) {
        this.serializer = new KryoSerializer(genericClass);
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int magic = in.readInt();
        if(magic != MAGIC_NUMBER){
            log.error("不识别的协议包：{}", magic);
            throw new Exception("UNKNOWN_PROTOCOL");
        }
        int dataLength = in.readInt();
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = serializer.deserialize(data);
        //添加到对象列表
        out.add(obj);
    }
}
