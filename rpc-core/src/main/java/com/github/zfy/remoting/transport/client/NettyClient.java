package com.github.zfy.remoting.transport.client;

import com.github.zfy.codec.MessageDecoder;
import com.github.zfy.codec.MessageEncoder;
import com.github.zfy.dto.RpcRequest;
import com.github.zfy.dto.RpcResponse;
import com.github.zfy.remoting.future.FutureHolder;
import com.github.zfy.remoting.future.RpcFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class NettyClient {
    private Channel channel;

    public synchronized Object sendRpcRequest(RpcRequest rpcRequest) {
        ChannelFuture channelFuture = channel.writeAndFlush(rpcRequest);
        RpcFuture rpcFuture = new RpcFuture(channelFuture.channel().eventLoop());
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                channel.closeFuture().addListener((ChannelFutureListener) closefuture -> {
                    log.info("连接成功");
                });
                FutureHolder.registerFuture(rpcRequest.getRequestId(), rpcFuture);
            } else {
                rpcFuture.tryFailure(future.cause());
            }
        });
        RpcResponse rpcResponse = null;
        try {
            //没用listener和getNow的方式是因为客户端是同步的，同时简便实现
            rpcResponse = rpcFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rpcResponse.getResult() != null) {
            return rpcResponse.getResult();
        } else {
            return null;
        }
    }

    //初始化客户端
    public void initClient(String hostname, int port) {
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
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
            this.channel = bootstrap.connect(hostname, port).sync().channel();
            log.info("客户端建立连接~~");
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