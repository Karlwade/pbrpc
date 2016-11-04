package dos.pbrpc.io;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import dos.pbrpc.RpcContext;
import dos.pbrpc.RpcException;
import dos.pbrpc.RpcMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import sofa.pbrpc.SofaRpcMeta;
import sofa.pbrpc.SofaRpcMeta.RpcMeta;

/**
 * process the out to protobuf object
 */
public class FrameDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(FrameDecoder.class);
    private byte[] primaryMagic = new byte[] { 'S', 'O', 'F', 'A' };
    private RpcContext context;

    public FrameDecoder(RpcContext context) {
        this.context = context;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf buffer = in;
        ByteBuf litBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
        boolean keyMatched = processMagicKey(litBuffer, out);
        if (!keyMatched) {
            return;
        }
        int metaSize = litBuffer.readInt();
        long dataSize = litBuffer.readLong();
        long totalSize = litBuffer.readLong();
        buffer.readerIndex(24);
        RpcMeta responseMeta = SofaRpcMeta.RpcMeta.parseFrom(new ByteBufInputStream(buffer, metaSize));
        logger.debug("meta size {} data size {} total size {} seq {} of frame", metaSize, dataSize, totalSize,
                responseMeta.getSequenceId());
        RpcMessageContext rpcMessageContext = context.get(responseMeta.getSequenceId());
        if (rpcMessageContext == null) {
            rpcMessageContext = new RpcMessageContext();
            RpcException ex = new RpcException(-1, "no frame with sequence id" + responseMeta.getSequenceId());
            ex.fillInStackTrace();
            rpcMessageContext.setException(ex);
            out.add(ex);
            return;
        }
        rpcMessageContext.setRequestMeta(responseMeta);
        buffer.readerIndex(24 + metaSize);
        if (dataSize > Integer.MAX_VALUE) {
            RpcException ex = new RpcException(-1, "too big frame");
            ex.fillInStackTrace();
            rpcMessageContext.setException(ex);
            out.add(ex);
            return;
        }
        Message response = rpcMessageContext.getResponse().getParserForType()
                .parseFrom(new ByteBufInputStream(buffer, (int) dataSize));
        rpcMessageContext.setResponse(response);
        out.add(rpcMessageContext);
    }

    private boolean processMagicKey(ByteBuf litBuffer, List<Object> out) {
        byte[] magic = new byte[4];
        litBuffer.readBytes(magic, 0, 4);
        boolean match = Arrays.equals(magic, primaryMagic);
        if (!match) {
            RpcMessageContext rpcMessageContext = new RpcMessageContext();
            RpcException ex = new RpcException(-1, "magic key is not sofa, server internal error");
            ex.fillInStackTrace();
            rpcMessageContext.setException(ex);
            out.add(rpcMessageContext);
        }
        return match;
    }
}
