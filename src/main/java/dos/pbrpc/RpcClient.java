package dos.pbrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private NettyConnection connection = null;

    private RpcClient(NettyConnection connection) {
        this.connection = connection;
    }

    public static RpcClient build(String host, int port) {
        NettyConnection conn = new NettyConnection();
        conn.start(host, port);
        RpcClient client = new RpcClient(conn);
        return client;
    }
    

}
