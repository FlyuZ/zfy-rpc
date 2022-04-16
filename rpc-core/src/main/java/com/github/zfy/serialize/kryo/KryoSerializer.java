package com.github.zfy.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.github.zfy.serialize.SerializeException;
import com.github.zfy.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Kryo serialization class, Kryo serialization efficiency is very high, but only compatible with Java language
 *
 * @author zfy
 * @createTime 2022.4.8
 */
@Slf4j
public class KryoSerializer implements Serializer {
    private Class<?> genericClass;

    public KryoSerializer(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    //使用ThreadLocal初始化Kryo，因为Kryo中的output和input是线程不安全的
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(genericClass);
        //设置是否注册全限定名
        //不强制要求注册类，默认为false，若设置为true则要求涉及到的所有类都要注册，包括jdk中的比如Object
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            // Object->byte:将对象序列化为byte数组
            kryo.writeClassAndObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            log.error("序列化时错误：" + e);
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            // byte->Object:从byte数组中反序列化出对对象
            Object o = kryo.readClassAndObject(input);
            kryoThreadLocal.remove();
            return o;
        }catch (Exception e){
            log.error("反序列化时错误：" + e);
            throw new SerializeException("Deserialization failed");
        }

    }
}
