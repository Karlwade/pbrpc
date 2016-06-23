package dos.des.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dos.des.algorithm.CalcPai;
import dos.des.algorithm.CalcPaiService;
import dos.des.algorithm.Request;
import dos.des.algorithm.Response;
import dos.parallel.ParallelEngine;

import ch.qos.logback.classic.Level;


public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private ParallelEngine engine;
    private ExecutorService executor = Executors.newFixedThreadPool(50);
    public Client() {
        String limit = System.getProperty("limit");
        if (limit == null) {
            limit = "52428800";
        }
        engine =  new ParallelEngine(4* 1024);
        engine.bootServer("0.0.0.0", 9526);
        engine.addNodes("127.0.0.1", 9527);
    }
    public void doRemoteTest(int count) {
        long now = System.currentTimeMillis();
        List<Future<Response>> results = new ArrayList<Future<Response>>();
        for (int i = 0; i < count; i++) {
            Request request = new Request();
            request.setData(ByteBuffer.allocate(3* 1024).array());
            Future<Response> ret = engine.submit(CalcPaiService.class, "calc", new Object[]{request});
            results.add(ret);
        }
        for (Future<Response> f : results) {
            try {
                f.get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.info("remote test {}", System.currentTimeMillis() - now);
    }
    
    
    public void doLocalTest(int count) {
        long now = System.currentTimeMillis();
        List<Future<ByteBuffer>> result = new ArrayList<Future<ByteBuffer>>();
        for (int i = 0; i < count; i ++) {
            ByteBuffer request = ByteBuffer.allocate(3* 1024);
            Future<ByteBuffer> f = executor.submit(new Callable<ByteBuffer>() {
                @Override
                public ByteBuffer call() throws Exception {
                    CalcPai.doCalcPi();
                    return ByteBuffer.allocate(1024);
                }});
            result.add(f);
        }
        for (Future<ByteBuffer> f : result) {
            try {
                f.get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        logger.info("test {} task with latency {} by local ", count, System.currentTimeMillis() - now);
    }
    
    
    
    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        Client client = new Client();
        while (true) {
            client.doRemoteTest(50000);
            client.doLocalTest(50000);
        }
    }

}
