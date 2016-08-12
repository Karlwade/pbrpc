package dos.executor.client;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientMonitor.class);
    private static AtomicLong sendCounter = new AtomicLong(0);
    private static AtomicLong receiveCounter = new AtomicLong(0);
    private static AtomicLong inBandCounter = new AtomicLong(0);
    private static AtomicLong outBandCounter = new AtomicLong(0);
    
    public static void incSend(final Long delta) {
        sendCounter.addAndGet(delta);
    }
    
    public static void incReceive(final Long delta) {
        receiveCounter.addAndGet(delta);
    }
    
    public static void incInBind(final Long delta) {
        inBandCounter.addAndGet(delta);
    } 
    
    public static void incOutBind(final Long delta) {
        outBandCounter.addAndGet(delta);
    }
    
}
