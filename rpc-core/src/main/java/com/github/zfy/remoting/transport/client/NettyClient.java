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
import io.netty.handler.timeout.IdleStateHandler;
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
        try {
            //将新请求放入未处理完的请求中
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //向服务端发请求，并设置监听，关于writeAndFlush()的具体实现可以参考：https://blog.csdn.net/qq_34436819/article/details/103937188
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info(String.format("客户端发送消息: %s", rpcRequest));
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("发送消息时发生错误: ", future.cause());
                }
            });
        }catch (Exception e){
            //将请求从请求集合中移除
            unprocessedRequests.remove(rpcRequest.getRequestId());
            log.error(e.getMessage(), e);
            //interrupt()这里作用是给受阻塞的当前线程发出一个中断信号，让当前线程退出阻塞状态，好继续执行然后结束
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

    //初始化客户端
    public void initClient(String hostname, int port) {
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟。
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel(SocketChannel ch) throws Exception {
                                 ChannelPipeline pipeline = ch.pipeline();
                                 //设定IdleStateHandler心跳检测每5秒进行一次写检测，如果5秒内write()方法未被调用则触发一次userEventTrigger()方法
                                 //实现客户端每5秒向服务端发送一次消息
                                 pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                                 pipeline.addLast(new MessageEncoder(RpcRequest.class));
                                 pipeline.addLast(new MessageDecoder(RpcResponse.class));
                                 pipeline.addLast(new NettyClientHandler());
                             }
                         }
                );
        try {

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