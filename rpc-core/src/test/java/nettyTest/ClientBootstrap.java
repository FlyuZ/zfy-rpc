package nettyTest;

import com.github.zfy.proxy.RpcClientProxy;
import com.github.zfy.remoting.transport.client.NettyClient;


public class ClientBootstrap {
    //这里定义协议头
    public static final String providerName = "HelloService";
    public static void main(String[] args) throws  Exception{
        //        //创建一个消费者
        NettyClient customer = new NettyClient();
        customer.initClient("127.0.0.1", 9998);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(customer);

        //创建代理对象
        HelloService service = rpcClientProxy.getProxy(HelloService.class);

        for (int i = 1; i <= 10; i++) {
            System.out.println(service.hello("c" + i));
        }
    }
}
