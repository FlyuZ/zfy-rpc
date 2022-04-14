package com.github.zfy.remoting.transport.client;

import com.github.zfy.dto.RpcResponse;
import com.github.zfy.remoting.future.FutureHolder;
import com.github.zfy.remoting.future.RpcFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    //与服务器的连接创建后，就会被调用, 这个方法是第一个被调用(1)
    /**
     * 请求数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(" channelActive 被调用  ");
    }

    //收到服务器的数据后，调用方法
    //
    // * 服务器返回数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        RpcFuture rpcFuture = FutureHolder.getAndRemoveFuture(rpcResponse.getRequestId());
        if (rpcFuture != null) {
            rpcFuture.setSuccess(rpcResponse);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}