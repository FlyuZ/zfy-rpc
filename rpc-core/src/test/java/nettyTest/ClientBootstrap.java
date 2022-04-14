package nettyTest;

import com.github.zfy.proxy.RpcClientProxy;
import com.github.zfy.remoting.transport.client.NettyClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientBootstrap {
    //这里定义协议头
    public static final String providerName = "HelloService";
    public static void main(String[] args) throws  Exception{
        //        //创建一个消费者
        NettyClient customer = new NettyClient();
        customer.initClient("127.0.0.1", 7000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(customer);

        //创建代理对象
        HelloService service = rpcClientProxy.getProxy(HelloService.class);

        for (int i = 1; i <= 10; i++) {
            log.info(service.hello("c" + i));
        }
    }
}
