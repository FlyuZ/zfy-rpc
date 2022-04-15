package com.github.zfy.remoting.transport.client;

import com.github.zfy.codec.MessageDecoder;
import com.github.zfy.codec.MessageEncoder;
import com.github.zfy.dto.RpcRequest;
import com.github.zfy.dto.RpcResponse;

import com.github.zfy.utils.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class NettyClient {
    private final UnprocessedRequests unprocessedRequests;
    private Channel channel;

    public NettyClient(){
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public  CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);

        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
            } else {
                future.channel().close();
                resultFuture.completeExceptionally(future.cause());
                log.error("发送消息时发生错误: ", future.cause());
            }
        });
        return resultFuture;
    }

    //初始化客户端
    public void initClient(String hostname, int port) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel(SocketChannel ch) throws Exception {
                                 ChannelPipeline pipeline = ch.pipeline();
                                 pipeline.addLast(new MessageEncoder(RpcRequest.class));
                                 pipeline.addLast(new MessageDecoder(RpcResponse.class));
                                 pipeline.addLast(new NettyClientHandler());
                             }
                         }
                );
        try {
//            bootstrap.connect(hostname, port).addListener((ChannelFutureListener) future -> {
//                if (future.isSuccess()) {
//                    log.info("The client has connected [{}] successful!", hostname+port);
//                    completableFuture.complete(future.channel());
//                } else {
//                    throw new IllegalStateException();
//                }
//            });
            this.channel = bootstrap.connect(hostname, port).sync().channel();
            log.info("The client has connected [{}] successful!", hostname+port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (this.channel != null) {
            if (this.channel.isOpen()) {
                this.channel.close();
            }
        }
    }
}