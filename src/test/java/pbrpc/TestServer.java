package pbrpc;

import dos.parallel.ExchangeDescriptor.Exchange;
import dos.parallel.client.ClientConnection;
import dos.parallel.server.ServerExchange;

public class TestServer {

     public static void main(String[] args) {
         ServerExchange server = new ServerExchange();
         server.build("127.0.0.1", 8181);
         try {
             Thread.sleep(1000000000);
          } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
          }
     }

}
