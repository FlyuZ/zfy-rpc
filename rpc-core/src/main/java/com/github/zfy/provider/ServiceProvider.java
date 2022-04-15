package com.github.zfy.provider;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在服务器端注册需要的service类
 * @author zfy
 * @createTime 2022.4.11
 */
@Slf4j
public class ServiceProvider {
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    public <T> void addServiceProvider( String serviceName, T service) {
        if (registeredService.contains(serviceName))
            return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("向接口: {} 注册服务: {}", serviceName, service.getClass().getName());
    }

    public Object getServiceProvider(String serviceName) throws Exception {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            log.info("找不到服务");
            throw new Exception();
        }
        return service;
    }
}
