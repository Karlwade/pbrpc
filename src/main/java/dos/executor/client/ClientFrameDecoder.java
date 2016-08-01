package dos.executor.client;

import java.nio.ByteOrder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dos.executor.ExecutorDescriptor;
import dos.executor.ExecutorDescriptor.CallableTaskResult;
import dos.executor.Header;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ClientFrameDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ClientFrameDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf buffer = in;
        ByteBuf litBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] magic = new byte[2];
        litBuffer.readBytes(magic, 0, 2);
        boolean validateOk = validateMagic(magic);
        if (!validateOk) {
            throw new Exception("magic key is not mt");
        }
        long dataSize = litBuffer.readLong();
        CallableTaskResult result = ExecutorDescriptor.CallableTaskResult
                .parseFrom(new ByteBufInputStream(buffer, (int) dataSize));
        out.add(result);
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
