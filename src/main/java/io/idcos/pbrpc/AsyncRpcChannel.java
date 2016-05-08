package io.idcos.pbrpc;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.MethodDescriptor;

import sofa.pbrpc.SofaRpcMeta;
import sofa.pbrpc.SofaRpcMeta.RpcMeta;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;

public class AsyncRpcChannel implements RpcChannel {
    private static final Logger logger = LoggerFactory.getLogger(AsyncRpcChannel.class);
    private IRpcConnection conn = null;
    private AtomicLong counter = null;

    public AsyncRpcChannel(IRpcConnection conn, AtomicLong counter) {
        this.conn = conn;
        this.counter = counter;
    }

    public void callMethod(MethodDescriptor method, RpcController ctrl, Message request, Message response,
            RpcCallback<Message> done) {
        final long seq = counter.incrementAndGet();
        logger.debug("send async request with seq {}", seq);
        final RpcMessage msg = new RpcMessage();
        msg.setMethod(method.getFullName());
        msg.setSequenceId(seq);
        msg.setRequest(request);
        RpcMeta meta = SofaRpcMeta.RpcMeta.newBuilder().setMethod(method.getFullName()).setSequenceId(seq).build();
        msg.setReqmeta(meta);
        msg.setDone(done);
        this.conn.sendMsg(msg);
    }

}
