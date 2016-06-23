package dos.parallel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.RateLimiter;
import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.Output;

import dos.parallel.ExchangeDescriptor.Exchange;
import dos.parallel.ExchangeDescriptor.Exchange.Builder;
import dos.parallel.ExchangeDescriptor.ExchangeType;
import dos.parallel.client.ClientConnection;
import dos.parallel.client.ClientExchange;
import dos.parallel.server.ServerExchange;
import io.netty.channel.ChannelHandlerContext;

public class ParallelEngine implements IDoneCallback, ITaskProcessor{
    
    private static final Logger logger = LoggerFactory.getLogger(ParallelEngine.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ReentrantLock lock = new ReentrantLock();
    private AtomicLong gSequence = new AtomicLong(0);
    
    // save send to other nodes
    private Map<Long, ClientExchange> outBuffer = new HashMap<Long, ClientExchange>();
    
    // other node connections
    private Map<String, NodeInfo> clients = new HashMap<String, NodeInfo>();
    
    private ServerExchange serverExchange = null;
    
    private ExecutorService executor = Executors.newFixedThreadPool(6);
    
    private final AtomicLong taskRunning = new AtomicLong(0); 
    private final AtomicLong taskCompleted = new AtomicLong(0); 
    
    private final AtomicLong lastTaskCompleted = new  AtomicLong(0); 
    private RateLimiter rateLimiter = null;
    private Iterator<String> nodeIterator = null;
    public ParallelEngine(double trafficLimit) {
       rateLimiter = RateLimiter.create(trafficLimit);
    }
    
    private void logStatus() {
        Runnable logStatusTask = new Runnable(){
            @Override
            public void run() {
               long current  = taskCompleted.get();
               long qps = current - lastTaskCompleted.get();
               lastTaskCompleted.set(current);
               logger.info("engine with qps {} and running task {}", qps, taskRunning.get());
            }};
          
        //scheduler.scheduleWithFixedDelay(logStatusTask, 0,1000, TimeUnit.MILLISECONDS);
    }
    
    public void addNodes(String host, int port) {
        String endpoint = host + ":" + port;
        if (clients.containsKey(endpoint)) {
            return;
        }
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setHost(host);
        nodeInfo.setLoad(0.0);
        nodeInfo.setPort(port);
        ClientConnection conn = new ClientConnection(this, this);
        nodeInfo.setConn(conn);
        conn.build(host, port);
        this.clients.put(endpoint, nodeInfo);
    }
    
    public void bootServer(String host, int port) {
        serverExchange = new ServerExchange(this, this);
        serverExchange.build(host, port);
    } 
    
    
    @Override
    public void done(Long seq, Exchange exchange) {
        try {
            lock.lock();
            ClientExchange client = this.outBuffer.get(seq);
            client.setDoneExchange(exchange);
            this.outBuffer.remove(seq);
            lock.unlock();
            synchronized (client.getExchange()) {
                client.getDone().set(true);
                client.getExchange().notifyAll();
            }
        } finally {
        }
    }
    
    public void buildIterator() {
        nodeIterator = clients.keySet().iterator();
    }
    public <T> Future<T> submit(Class iface, String method, Object[] args) {
        Builder builder = Exchange.newBuilder();
        builder.setClass_(iface.getCanonicalName());
        builder.setMethod(method);
        builder.setSequence(gSequence.incrementAndGet());
        builder.setType(ExchangeType.kTask);
        for (int i = 0; i < args.length; i++) {
            Output out = ByteString.newOutput();
            try {
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(args[i]);
                builder.addArguments(out.toByteString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClientExchange clientExchange = new ClientExchange();
        clientExchange.setExchange(builder.build());
        ExchangeFuture<T> future = new ExchangeFuture<T>(clientExchange);
        try {
            lock.lock();
            this.outBuffer.put(clientExchange.getExchange().getSequence(), clientExchange);
            if (nodeIterator == null) {
                this.buildIterator();
            }
            if (!nodeIterator.hasNext()) {
                nodeIterator = clients.keySet().iterator();
            }
            String key = nodeIterator.next();
            NodeInfo nodeInfo = clients.get(key);
            lock.unlock();
            rateLimiter.acquire(clientExchange.getExchange().getSerializedSize());
            nodeInfo.getConn().submit(clientExchange.getExchange());
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return future;
    }

    @Override
    public void process(final ChannelHandlerContext ctx, final Exchange exchange) {
        executor.submit(new Runnable(){
            @Override
            public void run() {
                taskRunning.incrementAndGet();
                try {
                    Class serviceCLazz = Class.forName(exchange.getClass_());
                    Method[] allmethod = serviceCLazz.getDeclaredMethods();
                    for (Method m : allmethod) {
                        if (m.getName().equals(exchange.getMethod())) {
                            Object[] args = getArguments(exchange);
                            Object ret = m.invoke(serviceCLazz.newInstance(), args);
                            Builder builder = Exchange.newBuilder();
                            Output out = ByteString.newOutput();
                            ObjectOutputStream oos = new ObjectOutputStream(out);
                            oos.writeObject(ret);
                            builder.setResult(out.toByteString());
                            builder.setDone(true);
                            builder.setType(ExchangeType.kDone);
                            builder.setErrorCode(0);
                            builder.setSequence(exchange.getSequence());
                            Exchange done = builder.build();
                            ctx.channel().writeAndFlush(done);
                        }
                    }
                    taskRunning.decrementAndGet();
                    taskCompleted.incrementAndGet();
                } catch (InstantiationException e) {
                    
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
            
        });
        
    }
    
    private Object[] getArguments(Exchange exchange) {
        Object[] arguments = new Object[exchange.getArgumentsCount()];
        for (int i = 0; i < exchange.getArgumentsCount(); i++) {
            try {
                ObjectInputStream ois = new ObjectInputStream(exchange.getArguments(i).newInput());
                arguments[i] = ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return arguments;
        
    }
    
    
    public void close() {
        if (serverExchange != null) {
            serverExchange.close();
        }
        Iterator<String> it = clients.keySet().iterator();
        while (it.hasNext()) {
            String endpoint = it.next();
            NodeInfo nodeInfo = clients.get(endpoint);
            nodeInfo.getConn().close();
        }
    }
    
}
