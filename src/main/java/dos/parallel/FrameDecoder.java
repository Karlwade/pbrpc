package dos.parallel;

import java.nio.ByteOrder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import dos.parallel.ExchangeDescriptor.Exchange;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


public class FrameDecoder extends ByteToMessageDecoder {
    
    private static final Logger logger = LoggerFactory.getLogger(FrameDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Profiler profiler = new Profiler("DECODE FRAME");
        profiler.setLogger(logger);
        ByteBuf buffer = in;
        profiler.start("decode header");
        ByteBuf litBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] magic = new byte[2];
        litBuffer.readBytes(magic, 0, 2);
        boolean validateOk = validateMagic(magic);
        if (!validateOk) {
            throw new Exception("magic key is not mt");
        }
        char type = litBuffer.readChar();
        long dataSize = litBuffer.readLong();
        logger.debug("data size {} of frame",  dataSize);
        profiler.start("decode ");
        Exchange exchange = ExchangeDescriptor.Exchange.parseFrom(new ByteBufInputStream(buffer, (int) dataSize));
        profiler.stop().log();
        out.add(exchange);
    }

    private boolean validateMagic(byte[] headerBuffer) {
        for (int i = 0; i < headerBuffer.length; i++) {
            if (headerBuffer[i] != Header.MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
}
