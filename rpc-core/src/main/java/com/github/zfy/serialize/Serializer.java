package com.github.zfy.serialize;


public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @return 反序列化的对象
     */
   Object deserialize(byte[] bytes);
}
