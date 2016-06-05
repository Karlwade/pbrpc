package pbrpc;


import dos.parallel.ParallelEngine;

public class TestServer {

     public static void main(String[] args) {
         ParallelEngine engine1 = new ParallelEngine();
         engine1.bootServer("127.0.0.1", 9527);
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
