package io.idcos.pbrpc;

import java.util.concurrent.atomic.AtomicBoolean;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;

import sofa.pbrpc.SofaRpcMeta.RpcMeta;

public class RpcMessage {
	private RpcMeta reqmeta;
	private RpcMeta resmeta;
	private Message request;
	private Message response;
	private long sequenceId;
	private String method;
	private RpcCallback<Message> done;
	private AtomicBoolean complete;
	public RpcMessage() {
		complete = new AtomicBoolean();
		complete.set(false);
	}

	public AtomicBoolean getComplete() {
		return complete;
	}

	public RpcCallback<Message> getDone() {
		return done;
	}

	public void setDone(RpcCallback<Message> done) {
		this.done = done;
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

	public RpcMeta getReqmeta() {
		return reqmeta;
	}

	public void setReqmeta(RpcMeta reqmeta) {
		this.reqmeta = reqmeta;
	}

	public RpcMeta getResmeta() {
		return resmeta;
	}

	public void setResmeta(RpcMeta resmeta) {
		this.resmeta = resmeta;
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

}
