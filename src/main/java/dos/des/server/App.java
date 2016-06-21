package dos.des.server;

import dos.parallel.ParallelEngine;

public class App {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ParallelEngine engine1 = new ParallelEngine();
        engine1.bootServer("0.0.0.0", 9527);
        while(true) {
            try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
        }
    }

}
