package dos.storage;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.tair3.client.TairClient.TairOption;
import com.taobao.tair3.client.error.TairFlowLimit;
import com.taobao.tair3.client.error.TairRpcError;
import com.taobao.tair3.client.error.TairTimeout;
import com.taobao.tair3.client.impl.DefaultTairClient;

public class GeoHashDistanceStorage {
    private static final Logger logger = LoggerFactory.getLogger(GeoHashDistanceStorage.class);
    private DefaultTairClient tairClient;
    private short ns;
    
    public void put(GeoHashDistance distance) {
        TairOption tairOption = new TairOption();
        tairOption.setTimeout(500);
        StringBuilder value = new StringBuilder();
        value.append(distance.getOrigin()).append(",")
        .append(distance.getDestination()).append(":")
        .append(distance.getDistance()).append(",")
        .append(distance.getRdistance());
        StringBuilder key = new StringBuilder();
        key.append(distance.getOrigin()).append(distance.getDestination());
        try {
            String prefix = distance.getCommonPrefix();
            if (prefix.length() >= 3) {
                tairClient.prefixPut(ns, distance.getCommonPrefix().getBytes(StandardCharsets.UTF_8),
                        key.toString().getBytes(StandardCharsets.UTF_8),
                        value.toString().getBytes(StandardCharsets.UTF_8), tairOption);
            }else {
                tairClient.put(ns, key.toString().getBytes(StandardCharsets.UTF_8),
                        value.toString().getBytes(StandardCharsets.UTF_8), tairOption);
            }
            
        } catch (TairRpcError e) {
            
            e.printStackTrace();
        } catch (TairFlowLimit e) {
            
            e.printStackTrace();
        } catch (TairTimeout e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void setTairClient(DefaultTairClient tairClient) {
        this.tairClient = tairClient;
    }
    public void setNs(short ns) {
        this.ns = ns;
    }
    
}
