package com.github.zfy.remoting.transport.server;

import com.github.zfy.codec.MessageDecoder;
import com.github.zfy.codec.MessageEncoder;
import com.github.zfy.dto.RpcRequest;
import com.github.zfy.dto.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zfy
 * @createTime 2022.4.10
 */
@Slf4j
public class NettyServer {
    //编写一个方法，完成对NettyServer的初始化和启动
    public static void startServer(String hostname, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel ch) throws Exception {
                                          ChannelPipeline pipeline = ch.pipeline();
                                          pipeline.addLast(new MessageDecoder(RpcRequest.class));
                                          pipeline.addLast(new MessageEncoder(RpcResponse.class));
                                          pipeline.addLast(new NettyServerHandler()); //业务处理器
                                      }
                                  }
                    );
            ChannelFuture channelFuture = serverBootstrap.bind(hostname, port).sync();
            log.info("服务提供方开始提供服务");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
