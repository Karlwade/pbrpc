package dos.executor.client;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dos.executor.ExecutorDescriptor;
import dos.executor.Header;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientFrameEncoder extends MessageToByteEncoder<ExecutorDescriptor.CallableTask> {
    private static final Logger logger = LoggerFactory.getLogger(ClientFrameEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ExecutorDescriptor.CallableTask task, ByteBuf out)
            throws Exception {
        ByteBuf header = out.alloc().directBuffer(Header.HEADER_SIZE);
        header = header.order(ByteOrder.LITTLE_ENDIAN);
        header.writeBytes(Header.MAGIC);
        header.writeLong(task.getSerializedSize());
        out.writeBytes(header);
        task.writeTo(new ByteBufOutputStream(out));
        header.release();
    }
}
