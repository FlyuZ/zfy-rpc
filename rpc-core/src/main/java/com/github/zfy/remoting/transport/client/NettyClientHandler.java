package com.github.zfy.remoting.transport.client;


import com.github.zfy.dto.RpcResponse;
import com.github.zfy.utils.SingletonFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import lombok.extern.slf4j.Slf4j;



/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    //收到服务器的数据后，调用方法
    //
    // * 服务器返回数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        log.info(String.format("客户端接收到消息: %s", rpcResponse));
        unprocessedRequests.complete(rpcResponse);
//        RpcFuture rpcFuture = FutureHolder.getAndRemoveFuture(rpcResponse.getRequestId());
//        if (rpcFuture != null) {
//            rpcFuture.setSuccess(rpcResponse);
//        }
    }
    //心跳机制 暂时先不加
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleState state = ((IdleStateEvent) evt).state();
//            if (state == IdleState.WRITER_IDLE) {
//                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
//                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(), CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
//                RpcRequest rpcRequest = new RpcRequest();
//                rpcRequest.setHeartBeat(true);
//                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}