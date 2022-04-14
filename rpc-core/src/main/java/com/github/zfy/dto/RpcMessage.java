package com.github.zfy.dto;

import lombok.*;

/**
 * 再封一层 主要是因为通讯协议内容要统一，封一层方便管理
 * @author zfy
 * @createTime 2922.4.14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    /**
     * rpc message type
     */
    private byte messageType;
//    /**
//     * serialization type
//     */
//    private byte codec;
//    /**
//     * compress type
//     */
//    private byte compress;
    /**
     * request id
     */
    private int requestId;
    /**
     * request data
     */
    private Object data;
}
