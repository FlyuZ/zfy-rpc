package com.github.zfy.remoting.transport.server;

import com.github.zfy.dto.RpcRequest;
import com.github.zfy.dto.RpcResponse;
import com.github.zfy.provider.ServiceProvider;
import com.github.zfy.utils.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zfy
 * @createTime 2022.4.11
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Map<String, Object> CLASS_MAP = new HashMap<>();

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProvider.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        if(rpcRequest.getHeartBeat()){
            log.info("接收到客户端心跳包……");
            return;
        }
        log.info("获取从客户端发送的消息" + rpcRequest);
        RpcResponse response = handle(rpcRequest);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                log.info("长时间未收到心跳包，断开连接……");
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常时关闭连接。
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }

    private RpcResponse handle(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        String className = rpcRequest.getClassName();
        try {
            Object clazz = serviceProvider.getServiceProvider(rpcRequest.getClassName());

            Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = null;
            result = method.invoke(clazz, rpcRequest.getParameters());
            rpcResponse.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }
}