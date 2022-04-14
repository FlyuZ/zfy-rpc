import com.github.zfy.dto.RpcRequest;
import com.github.zfy.serialize.kryo.KryoSerializer;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class KryoSerializerTest {
    private RpcRequest sayHello = RpcRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .build();

    @Test
    public void testKryo() {

        KryoSerializer kryoSerializer = new KryoSerializer(RpcRequest.class);
        byte[]  ResSerializer = kryoSerializer.serialize(sayHello);
//        byte[]  ResSerializer = getFromFile();
        RpcRequest obj = (RpcRequest) kryoSerializer.deserialize(ResSerializer);
        System.out.println(obj.toString());
    }
    public void writeToFile(byte[] obj) throws IOException {
        Files.write(Paths.get("file.txt"), obj);
    }
    public void getFromFile() throws IOException {
//        byte[] obj = Files.readAllBytes(Path.of("file.txt"));

    }
}
