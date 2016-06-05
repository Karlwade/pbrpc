package dos.parallel;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.Output;

import dos.parallel.ExchangeDescriptor.Exchange;
import dos.parallel.ExchangeDescriptor.Exchange.Builder;
import dos.parallel.ExchangeDescriptor.ExchangeType;
import dos.parallel.client.ClientConnection;
import dos.parallel.client.ClientExchange;
import dos.parallel.server.ServerExchange;

public class ParallelEngine implements IDoneCallback{
    
    private ReentrantLock lock = new ReentrantLock();
    private AtomicLong gSequence = new AtomicLong(0);
    
    // save send to other nodes
    private Map<Long, ClientExchange> outBuffer = new HashMap<Long, ClientExchange>();
    
    // other node connections
    private Map<String, NodeInfo> clients = new HashMap<String, NodeInfo>();
    
    private ServerExchange serverExchange = null;
    
    public void addNodes(String host, int port) {
        String endpoint = host + ":" + port;
        if (clients.containsKey(endpoint)) {
            return;
        }
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setHost(host);
        nodeInfo.setLoad(0.0);
        nodeInfo.setPort(port);
        ClientConnection conn = new ClientConnection(this);
        nodeInfo.setConn(conn);
        this.clients.put(endpoint, nodeInfo);
    }
    
    public void bootServer(String host, int port) {
        serverExchange = new ServerExchange(this);
        serverExchange.build(host, port);
    } 
    
    @Override
    public void done(Long seq, Exchange exchange) {
        try {
            lock.lock();
            ClientExchange client = this.outBuffer.get(seq);
            client.setDoneExchange(exchange);
            lock.unlock();
            synchronized (client.getExchange()) {
                client.getDone().set(true);
                client.getExchange().notifyAll();
            }
        } finally {
        }
    }
    
    public <T> Future<T> submit(Class iface, Method method, Object[] args) {
        Builder builder = Exchange.newBuilder();
        builder.setClass_(iface.getCanonicalName());
        builder.setMethod(method.getName());
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
            NodeInfo nodeInfo = clients.get(0);
            nodeInfo.getConn().submit(clientExchange.getExchange());
        } finally {
            lock.unlock();
        }
        return future;
    }
    
}
