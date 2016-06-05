package dos.parallel;

import dos.parallel.ExchangeDescriptor.Exchange;

public interface IDoneCallback {

    public void done(Long seq, 
                     Exchange exchange);
    
}
