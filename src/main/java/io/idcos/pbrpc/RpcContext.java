package io.idcos.pbrpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContext {

	private Map<Long, RpcMessage> context = new ConcurrentHashMap<Long, RpcMessage>();
	
	public void put(Long id, RpcMessage msg) {
		context.put(id, msg);
	}
	
	public RpcMessage get(Long id) {
		return context.get(id);
	}
	
	public void erase(Long id) {
		context.remove(id);
	}
}
