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

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}