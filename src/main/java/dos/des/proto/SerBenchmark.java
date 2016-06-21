package dos.des.proto;

import java.nio.ByteBuffer;

import dos.des.algorithm.Request;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class SerBenchmark {

    
    
    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        LinkedBuffer buffer = LinkedBuffer.allocate(10*1024);
        Schema<Request> schema = RuntimeSchema.getSchema(Request.class);
        for (int i = 0; i < 50000; i++) {
            Request req = new Request();
            req.setData(ByteBuffer.allocate(3*1024).array());
            ProtobufIOUtil.toByteArray(req, schema, buffer);
            buffer.clear();
        }
        System.out.println(System.currentTimeMillis() - now);
    }

}
