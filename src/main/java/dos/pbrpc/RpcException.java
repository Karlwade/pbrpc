package dos.pbrpc;

/**
 * Created by imotai on 2016/11/3.
 */

public class RpcException extends Exception {
    private int code;
    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }
}
