package dos.parallel;

public class Header {

    // 8 bytes data
    // 2 bytes magic key
    // 2 bytes message type
    public final static int HEADER_SIZE = 12;
    public final static char MESSAGE_TASK = 1;
    public final static char MESSAGE_DONE = 2;
    public final static byte[] MAGIC = new byte[] { 77, 84 };
}
