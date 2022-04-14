import nettyTest.HelloService;
import nettyTest.HelloServiceImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestMethod {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        HelloService helloService = new HelloServiceImpl();
        map.put("1", helloService);
        Object obj = map.get("1");
        System.out.println(Arrays.toString(obj.getClass().getMethods()));
        Method method = obj.getClass().getMethod("hello", String.class);
        Object result = method.invoke(obj, "11111");
    }
}
