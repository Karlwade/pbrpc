package dos.des.server;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import dos.parallel.ParallelEngine;

public class App {

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
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
