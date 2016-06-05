package pbrpc;


import java.awt.Point;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dos.parallel.ParallelEngine;

public class TestServer2 {

     public static void main(String[] args) {
         ParallelEngine engine2 = new ParallelEngine();
         engine2.bootServer("127.0.0.1", 9526);
         engine2.addNodes("127.0.0.1", 9527);
         Point point1 = new Point();
         point1.setLocation(1, 1);
         
         Point point2 = new Point();
         point2.setLocation(2, 2);
         boolean running = true;
         long cout  = 1000000;
         while (cout > 0 ) {
             Future<Point> ret = engine2.submit(CalculateDistanceService.class,  "calc", new Object[]{point1, point2});
             cout--;
         }
         engine2.close();
     }

}
