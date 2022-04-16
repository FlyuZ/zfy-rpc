package com.github.zfy.provider;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zfy
 * @createTime 2022.4.11
 * @description 默认的服务注册表，保存服务端本地服务
 */
@Slf4j
public class ServiceProvider {

    /**
     * key = 服务名称(即接口名), value = 服务实体(即实现类的实例对象)
     */
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
