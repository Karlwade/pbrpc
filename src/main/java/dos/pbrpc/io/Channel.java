package dos.pbrpc.io;

import dos.pbrpc.Message;
import dos.pbrpc.RpcMessageContext;

/**
 * Created by imotai on 2016/11/3.
 */
public interface Channel {
    void sendMessage(RpcMessageContext m);
}
