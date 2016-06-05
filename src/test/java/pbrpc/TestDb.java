package pbrpc;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class TestDb {

    public static void main(String[] args) {
        DB db = DBMaker.fileDB("db2").make();
        long counter = 0;
        BTreeMap<String, String> treeMap = db.treeMap("delay1").keySerializer(Serializer.STRING)
                                                              .valueSerializer(Serializer.STRING)
                                                              .createOrOpen();
        System.out.println(System.currentTimeMillis());
        for (long i = 0 ; i < 1000000; i++) {
            treeMap.put(String.valueOf(i), String.valueOf(i));
        }
        System.out.println(System.currentTimeMillis());

    }

}
