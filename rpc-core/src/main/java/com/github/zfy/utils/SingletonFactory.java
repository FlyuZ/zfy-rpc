package com.github.zfy.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * 单例工厂
 * @author zfy
 * @createTime 2022.4.14
 */
public class SingletonFactory {

    private static  Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {}

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if(instance == null) {
                try {
                    instance = clazz.getDeclaredConstructor().newInstance();
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }

}