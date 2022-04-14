package com.github.zfy.remoting.transport.server;

import com.github.zfy.dto.RpcRequest;
import com.github.zfy.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zfy
 * @createTime 2022.4.11
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Map<String, Object> CLASS_MAP = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取客户端发送的消息，并调用服务
        Object obj = Class.forName("nettyTest.HelloServiceImpl").getDeclaredConstructor().newInstance();  //  这里的问题？？？？

        CLASS_MAP.put("nettyTest.HelloService", obj);
        log.info("msg=" + msg);
        RpcRequest request = (RpcRequest) msg;
        RpcResponse response = handle(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常时关闭连接。
        cause.printStackTrace();
        ctx.close();
    }

    private RpcResponse handle(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        String className = rpcRequest.getClassName();
        try {
            Object clazz = CLASS_MAP.get(className);
//            if(clazz != null) {
//                System.out.println(Arrays.toString(clazz.getClass().getMethods()));
//                System.out.println(rpcRequest.getMethodName());
//            }

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