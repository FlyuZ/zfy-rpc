package com.github.zfy.spi;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI类加载器帮助类
 * @author zfy
 * Date: 2022.4.8
 */
public class ServiceSupport {
    private final static Map<String, Object> singletonServices = new HashMap<>();

    public synchronized static <S> S load(Class<S> type) {
        return StreamSupport.
                stream(ServiceLoader.load(type).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .findFirst().orElseThrow(ServiceLoadException::new);
    }

    public synchronized static <S> Collection<S> loadAll(Class<S> type) {
        return StreamSupport.
                stream(ServiceLoader.load(type).spliterator(), false)
                .map(ServiceSupport::singletonFilter).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <S>  S singletonFilter(S service) {

        if(service.getClass().isAnnotationPresent(SPI.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
