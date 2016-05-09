package dos.pbrpc;

import dos.pbrpc.RpcMessage;

/**
 * rpc connection interface which sends message and records traffic
 */
public interface IRpcConnection {

    public void sendMsg(RpcMessage message);

    public void close();

}
