package dos.parallel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dos.parallel.ExchangeDescriptor.Exchange;
import dos.parallel.ExchangeDescriptor.ExchangeType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ExchangeProcessor extends SimpleChannelInboundHandler<Exchange> {
    
    private static final Logger logger = LoggerFactory.getLogger(ExchangeProcessor.class);
    private IDoneCallback doneCallback = null;
    private ITaskProcessor taskProcessor = null;
    public ExchangeProcessor(IDoneCallback doneCallback,
                             ITaskProcessor taskProcessor) {
        this.doneCallback = doneCallback;
        this.taskProcessor = taskProcessor;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, 
                                Exchange exchange) throws Exception {
        
        if (exchange.getType().equals(ExchangeDescriptor.ExchangeType.kTask)) {
            logger.debug("receive a task with sequence {}", exchange.getSequence());
            taskProcessor.process(ctx, exchange);
        }else if (exchange.getType().equals(ExchangeDescriptor.ExchangeType.kDone)) {
            logger.debug("receive a done task with sequence {}", exchange.getSequence());
            doneCallback.done(exchange.getSequence(), exchange);
        }
    }

}
