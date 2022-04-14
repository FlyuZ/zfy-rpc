package com.github.zfy.dto;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    private String requestId;

    private Object result;

}
