package com.github.zfy.proxy;

import com.github.zfy.dto.RpcRequest;

import com.github.zfy.dto.RpcResponse;
import com.github.zfy.remoting.transport.client.NettyClient;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zfy
 * @createTime 2022.4.11
 * @description Rpc客户端动态代理
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private final NettyClient nettyClient;
    public RpcClientProxy(NettyClient nettyClient){
        this.nettyClient = nettyClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        //创建代理对象
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * This method is actually called when you use a proxy object to call a method.
     * The proxy object is the object you get through the getProxy method.
     */
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .className(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .heartBeat(false)
                .build();
        //异步获取调用结果
        CompletableFuture<RpcResponse> completableFuture =  nettyClient.sendRpcRequest(rpcRequest);
        RpcResponse rpcResponse = completableFuture.get();
        return rpcResponse.getResult();
    }
}
