package dos.des.client;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dos.des.algorithm.CalcPaiService;
import dos.des.algorithm.Request;
import dos.des.algorithm.Response;
import dos.parallel.ParallelEngine;

public class Client {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ParallelEngine engine2 = new ParallelEngine();
        engine2.bootServer("0.0.0.0", 9526);
        engine2.addNodes("0.0.0.0", 9527);
        Request request = new Request();
        request.setData(ByteBuffer.allocate(3* 1024).array());
        Future<Response> ret = engine2.submit(CalcPaiService.class, "calc", new Object[]{request});
        try {
            Response response = ret.get();
            System.out.println(response.getData().length);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
