package dos.parallel;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FrameEncoder extends MessageToByteEncoder<ExchangeDescriptor.Exchange> {
    private static final Logger logger = LoggerFactory.getLogger(FrameEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, 
                          ExchangeDescriptor.Exchange exchange, 
                          ByteBuf out) throws Exception {
        Profiler profiler = new Profiler("ENCODE FRAME");
        profiler.setLogger(logger);
        profiler.start("put msg to context");
        ByteBuf header = out.alloc().directBuffer(Header.HEADER_SIZE);
        header = header.order(ByteOrder.LITTLE_ENDIAN);
        header.writeBytes(Header.MAGIC);
        if (exchange.getType().equals(ExchangeDescriptor.ExchangeType.kTask)) {
            header.writeChar(Header.MESSAGE_TASK);
        } else {
            header.writeChar(Header.MESSAGE_DONE);
        }
        header.writeLong(exchange.getSerializedSize());
        out.writeBytes(header);
        profiler.start("encode and write");
        exchange.writeTo(new ByteBufOutputStream(out));
        profiler.stop().log();
        header.release();
    }
}
