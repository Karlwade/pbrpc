package dos.parallel;

import dos.parallel.ExchangeDescriptor.Exchange;
import io.netty.channel.ChannelHandlerContext;

public interface ITaskProcessor {

    void process(ChannelHandlerContext ctx, 
                 Exchange exchange);
}
