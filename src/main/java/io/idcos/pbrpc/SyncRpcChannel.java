package io.idcos.pbrpc;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import sofa.pbrpc.SofaRpcMeta;
import sofa.pbrpc.SofaRpcMeta.RpcMeta;

public class SyncRpcChannel implements BlockingRpcChannel {

    private static final Logger logger = LoggerFactory.getLogger(SyncRpcChannel.class);
    private IRpcConnection conn = null;
    private AtomicLong counter = null;

    public SyncRpcChannel(IRpcConnection conn, AtomicLong counter) {
        this.conn = conn;
        this.counter = counter;
    }

    @Override
    public Message callBlockingMethod(MethodDescriptor method, RpcController controller, Message request,
            Message responsePrototype) throws ServiceException {

        final long seq = counter.incrementAndGet();
        logger.debug("send sync request with seq {}", seq);
        final RpcMessage msg = new RpcMessage();
        msg.setMethod(method.getFullName());
        msg.setSequenceId(seq);
        msg.setRequest(request);
        msg.setResponse(responsePrototype);
        RpcMeta meta = SofaRpcMeta.RpcMeta.newBuilder().setType(SofaRpcMeta.RpcMeta.Type.REQUEST)
                .setMethod(method.getFullName()).setSequenceId(seq).build();
        msg.setReqmeta(meta);
        RpcCallback<Message> done = new RpcCallback<Message>() {
            @Override
            public void run(Message response) {
                synchronized (msg) {
                    msg.notifyAll();
                }
                ;
            }
        };
        msg.setDone(done);
        this.conn.sendMsg(msg);
        while (!msg.getComplete().get()) {
            try {
                synchronized (msg) {
                    msg.wait();
                }
                ;
            } catch (InterruptedException e) {
            }
        }
        return msg.getResponse();
    }
}
