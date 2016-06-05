package pbrpc;

import dos.parallel.ExchangeDescriptor.Exchange;
import dos.parallel.client.ClientConnection;
import dos.parallel.server.ServerExchange;

public class TestExchange {

     public static void main(String[] args) {
         ClientConnection client = new ClientConnection();
         client.build("127.0.0.1", 8181);
         Exchange exchange = Exchange.newBuilder().setClass_("dasdasd").setErrorCode(111).build();
         client.submit(exchange);
         try {
             Thread.sleep(1000000000);
          } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
          }
     }

}
