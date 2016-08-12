package dos.executor.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.protostuff.Schema;

public class DistributedClient {
    private static final Logger logger = LoggerFactory.getLogger(DistributedClient.class);
    private volatile ChannelFuture channel = null;
    private AtomicLong sequenceId = new AtomicLong(0);
    private ConcurrentMap<String, Schema> schemaCache = new ConcurrentHashMap<String, Schema>();
    public void sendSyncTask(Callable task) {
        if (channel == null) {
            logger.error("channel in null");
        }
        channel.channel().pipeline().writeAndFlush(task);
        ClientMonitor.incSend(1l);
        ClientMonitor.incOutBind((long)task.getSerializedSize());
    }
}
