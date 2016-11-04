package dos.pbrpc;

import java.util.concurrent.atomic.AtomicBoolean;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;

import sofa.pbrpc.SofaRpcMeta.RpcMeta;

public class RpcMessageContext {

    private RpcMeta requestMeta;
    private RpcMeta responseMeta;
    private Message request;
    private Message response;
    private long sequenceId;
    private String method;
    private RpcCallback<Message> done;
    private AtomicBoolean complete = new AtomicBoolean(false);
    private RpcException exception = null;

    public RpcMessageContext() {
        complete = new AtomicBoolean();
        complete.set(false);
    }

    public RpcException getException() {
        return exception;
    }

    public void setException(RpcException exception) {
        this.exception = exception;
    }

    public RpcMeta getRequestMeta() {
        return requestMeta;
    }

    public void setRequestMeta(RpcMeta requestMeta) {
        this.requestMeta = requestMeta;
    }

    public RpcMeta getResponseMeta() {
        return responseMeta;
    }

    public void setResponseMeta(RpcMeta responseMeta) {
        this.responseMeta = responseMeta;
    }

    public Message getRequest() {
        return request;
    }

    public void setRequest(Message request) {
        this.request = request;
    }

    public Message getResponse() {
        return response;
    }

    public void setResponse(Message response) {
        this.response = response;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RpcCallback<Message> getDone() {
        return done;
    }

    public void setDone(RpcCallback<Message> done) {
        this.done = done;
    }

    public AtomicBoolean getComplete() {
        return complete;
    }

    public void setComplete(AtomicBoolean complete) {
        this.complete = complete;
    }
}
