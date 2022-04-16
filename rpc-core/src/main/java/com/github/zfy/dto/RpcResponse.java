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
    /**
     * 响应对应的请求号
     */
    private String requestId;
    /**
     *成功时的响应数据
     */
    private Object result;

}
