package dos.pbrpc.io;

import dos.pbrpc.Options;
import dos.pbrpc.RpcException;

/**
 * Created by imotai on 2016/11/3.
 */
public interface Connection {

    /**
     *
     * */
    void connect(Options opt, String host, int port) throws RpcException;

    Channel getChannel();

    void close();

}
