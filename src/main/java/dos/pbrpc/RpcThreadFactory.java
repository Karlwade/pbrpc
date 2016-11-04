package dos.pbrpc;

import org.mapdb.Atomic;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by imotai on 2016/11/3.
 */
public class RpcThreadFactory implements ThreadFactory {
    private String name;

    private AtomicLong counter = new AtomicLong(0);
    public RpcThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        String tname = name + "-" + counter.incrementAndGet();
        Thread t = new Thread(tname);
        return t;
    }
}
