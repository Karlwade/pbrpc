package dos.pbrpc.io;

import java.util.concurrent.atomic.AtomicLong;

import com.google.protobuf.*;
import dos.pbrpc.IRpcConnection;
import dos.pbrpc.RpcContext;
import dos.pbrpc.RpcMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.MethodDescriptor;
import sofa.pbrpc.SofaRpcMeta;

public class SyncRpcChannel implements BlockingRpcChannel {
    private static final Logger logger = LoggerFactory.getLogger(SyncRpcChannel.class);
    private Channel channel = null;
    private AtomicLong seq = null;
    private RpcContext context;
    public SyncRpcChannel(Channel channel, RpcContext context) {
        this.channel = channel;
        this.seq = new AtomicLong(0);
        this.context = context;
    }

    @Override
    public Message callBlockingMethod(Descriptors.MethodDescriptor method,
                                      RpcController controller,
                                      Message request,
                                      Message responsePrototype) throws ServiceException {
        long sequence = seq.incrementAndGet();
        final RpcMessageContext messageContext = new RpcMessageContext();
        messageContext.setSequenceId(sequence);
        messageContext.setRequest(request);
        SofaRpcMeta.RpcMeta meta = SofaRpcMeta.RpcMeta.newBuilder().setType(SofaRpcMeta.RpcMeta.Type.REQUEST)
                .setMethod(method.getFullName()).setSequenceId(sequence).build();
        messageContext.setRequestMeta(meta);
        messageContext.setDone(new RpcCallback<Message>() {
            @Override
            public void run(Message parameter) {
                synchronized (messageContext) {
                    messageContext.notifyAll();
                }
            }
        });
        context.put(sequence, messageContext);
        channel.sendMessage(messageContext);
        while (!messageContext.getComplete().get()) {
            synchronized (messageContext) {
                try {
                    messageContext.wait();
                } catch (InterruptedException e) {
                    logger.error("message with seq {} interrupted ", sequence, e);
                }
            }
        }
        //TODO respnse meta error handle
        context.erase(sequence);
        return messageContext.getResponse();
    }
}
