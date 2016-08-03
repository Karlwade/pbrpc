package dos.executor.client;

import java.util.concurrent.atomic.AtomicLong;

public class ClientMonitor {
    private AtomicLong sendQps = new AtomicLong(0);
    private AtomicLong receiveQps = new AtomicLong(0);
    private AtomicLong inBandwidth = new AtomicLong(0);
    private AtomicLong outBandwidth = new AtomicLong(0);
    
    
}
