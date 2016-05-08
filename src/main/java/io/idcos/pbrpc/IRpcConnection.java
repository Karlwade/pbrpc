package io.idcos.pbrpc;

import io.idcos.pbrpc.RpcMessage;

/**
 * rpc connection interface which sends message and records traffic
 */
public interface IRpcConnection {

    public void sendMsg(RpcMessage message);

    public void close();

}
