package dos.parallel.client;

import java.util.concurrent.atomic.AtomicBoolean;

import dos.parallel.ExchangeDescriptor.Exchange;

public class ClientExchange {

    private AtomicBoolean done = new AtomicBoolean(false);
    private Exchange exchange;
    private Exchange doneExchange;
    public AtomicBoolean getDone() {
        return done;
    }
    public void setDone(AtomicBoolean done) {
        this.done = done;
    }
    public Exchange getExchange() {
        return exchange;
    }
    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }
    public Exchange getDoneExchange() {
        return doneExchange;
    }
    public void setDoneExchange(Exchange doneExchange) {
        this.doneExchange = doneExchange;
    }
    
    
}
