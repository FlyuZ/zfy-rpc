package com.github.zfy.dto;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable{
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 待调用接口名称
     */
    private String className;
    /**
     * 待调用方法名称
     */
    private String methodName;
    /**
     * 待调用方法的参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 待调用方法的参数
     */
    private Object[] parameters;
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}