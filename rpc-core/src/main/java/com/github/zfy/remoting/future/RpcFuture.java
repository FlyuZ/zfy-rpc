package com.github.zfy.remoting.future;


import com.github.zfy.dto.RpcResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;

public class RpcFuture extends DefaultPromise<RpcResponse> {
    public RpcFuture(EventExecutor executor) {
        super(executor);
    }
}