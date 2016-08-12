package dos.executor.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dos.executor.ExecutorDescriptor;
import io.netty.channel.ChannelFuture;

public class DistributedClient {
    private static final Logger logger = LoggerFactory.getLogger(DistributedClient.class);
    private volatile ChannelFuture channel = null;
    
    public void sendTask(ExecutorDescriptor.CallableTask task) {
        if (channel == null) {
            logger.error("channel in null");
        }
        channel.channel().pipeline().writeAndFlush(task);
        ClientMonitor.incSend(1l);
        ClientMonitor.incOutBind((long)task.getSerializedSize());
    }
}
