package dos.storage;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.tair3.client.error.TairException;
import com.taobao.tair3.client.impl.DefaultTairClient;

public class EsImporter {
    private static final Logger logger = LoggerFactory.getLogger(EsImporter.class);
    private TransportClient transportClient;
    private DefaultTairClient tairClient;
    private ExecutorService executor = Executors.newFixedThreadPool(20);
    private GeoHashDistanceStorage storage = null;
    public void init() {
        storage = new GeoHashDistanceStorage();
        
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "waimai-c-bench").build();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("yf-waimai-c-es-staging05.yf.sankuai.com", 8415);
        List<InetSocketTransportAddress> servers = new ArrayList<InetSocketTransportAddress>();
        servers.add(new InetSocketTransportAddress(inetSocketAddress));
        transportClient = TransportClient.builder().settings(settings).build();
        for (InetSocketTransportAddress server : servers)
            transportClient.addTransportAddress(server);
        tairClient = new DefaultTairClient();
        tairClient.setMaster("10.32.178.141:8411");
        tairClient.setGroup("sandbox");
        tairClient.setLocalAppKey("local key");
        tairClient.setRemoteAppKey("remote");
        try {
            tairClient.init();
        } catch (TairException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        storage.setNs((short)1);
        storage.setTairClient(tairClient);
    }
    
    public void start() {
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse scrollResp = transportClient.prepareSearch("bm_distance")
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(1000).execute().actionGet();
        AtomicLong counter = new AtomicLong(0);
        while (true) {
            List<Future<Boolean>> fs = new ArrayList<Future<Boolean>>();
            for (final SearchHit hit : scrollResp.getHits().getHits()) {
                Future<Boolean> f = executor.submit(new Callable<Boolean>(){
                    @Override
                    public Boolean call() throws Exception {
                        GeoHashDistance distance = new GeoHashDistance();
                        distance.setOrigin(hit.getSource().get("origin").toString());
                        distance.setDestination(hit.getSource().get("destination").toString());
                        distance.setDistance(Double.parseDouble(hit.getSource().get("distance").toString()));
                        distance.setRdistance(Double.parseDouble(hit.getSource().get("reverse_distance").toString()));
                        storage.put(distance);
                        
                        return true;
                    }});
                fs.add(f);
            }
            
            for (Future<Boolean> f : fs) {
                try {
                    f.get(10, TimeUnit.SECONDS);
                    counter.incrementAndGet();
                    logger.info("write  turn {} successfully ", counter.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    f.cancel(true);
                }
            }
            scrollResp = transportClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                logger.info("write turn completed ");
                break;
            }
            
        }
    }
    
    public static void main(String[] args) {
        EsImporter importer = new EsImporter();
        importer.init();
        importer.start();
    }

}
