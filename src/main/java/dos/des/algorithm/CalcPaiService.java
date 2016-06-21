package dos.des.algorithm;

import java.nio.ByteBuffer;

public class CalcPaiService {

    public Response calc(Request req) {
        CalcPai.doCalcPi();
        Response response = new Response();
        response.setData(ByteBuffer.allocate(1024).array());
        return response;
    }
}
