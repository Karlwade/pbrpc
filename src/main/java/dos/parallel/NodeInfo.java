package dos.parallel;

import dos.parallel.client.ClientConnection;

public class NodeInfo {

    private String host;
    private int port;
    private double load;
    private ClientConnection conn = null;
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public double getLoad() {
        return load;
    }
    public void setLoad(double load) {
        this.load = load;
    }
    public ClientConnection getConn() {
        return conn;
    }
    public void setConn(ClientConnection conn) {
        this.conn = conn;
    }
    
    
}
