import com.github.zfy.serialize.kryo.KryoSerializer;
import org.junit.Test;

class SayHello{
    String msg = "hello";
    void say(){
        System.out.println(msg);
    }
}
public class KryoSerializerTest {
    private SayHello sayHello = new SayHello();

    @Test
    public void testKryo(){
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[]  ResSerializer = kryoSerializer.serialize(sayHello);
        SayHello obj = kryoSerializer.deserialize(ResSerializer, SayHello.class);
        obj.say();
    }
}
