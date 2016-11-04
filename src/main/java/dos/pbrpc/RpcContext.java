package dos.pbrpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContext {

    private Map<Long, RpcMessageContext> context = new ConcurrentHashMap<Long, RpcMessageContext>();

    public void put(Long id, RpcMessageContext msg) {
        context.put(id, msg);
    }

    public RpcMessageContext get(Long id) {
        return context.get(id);
    }

    public void erase(Long id) {
        context.remove(id);
    }
}
