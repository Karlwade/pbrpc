package dos.des.algorithm;

import java.io.Serializable;

public class Response implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -5223706954411430941L;
    public byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
}
