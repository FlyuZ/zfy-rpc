package com.github.zfy.remoting.transport.server;

import com.github.zfy.codec.MessageDecoder;
import com.github.zfy.codec.MessageEncoder;
import com.github.zfy.dto.RpcRequest;
import com.github.zfy.dto.RpcResponse;
import com.github.zfy.provider.ServiceProvider;
import com.github.zfy.utils.SingletonFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zfy
 * @createTime 2022.4.15
 */
@Slf4j
@Component
public class NettyServer {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9998;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProvider.class);
    public  <T> void registerService(T service) {
        String serviceName = service.getClass().getInterfaces()[0].getCanonicalName();
        serviceProvider.addServiceProvider(serviceName, service);
        log.info("服务器已注册: " + serviceName);
    }

    //编写一个方法，完成对NettyServer的初始化和启动
    public void startServer()  {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //初始化Netty服务端启动器，作为服务端入口
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //将主从“线程池”初始化到启动器中
            serverBootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道类型
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //  是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    // .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel ch) throws Exception {
                                          ChannelPipeline pipeline = ch.pipeline();
                                          // 责任链模式
                                          // 30 秒之内没有收到客户端请求的话就关闭连接
                                          pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                                          pipeline.addLast(new MessageDecoder(RpcRequest.class));
                                          pipeline.addLast(new MessageEncoder(RpcResponse.class));
                                          pipeline.addLast(new NettyServerHandler()); //业务处理器
                                      }
                                  }
                    );
            ChannelFuture channelFuture = serverBootstrap.bind(HOST, PORT).sync();
            log.info("服务提供方开始提供服务");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("启动服务器发生错误: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
