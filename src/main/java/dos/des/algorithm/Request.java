package dos.des.algorithm;

import java.io.Serializable;

public class Request implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -8400258626110288829L;
    public byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
}
