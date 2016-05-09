package dos.pbrpc;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcFrameEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final Logger logger = LoggerFactory.getLogger(RpcFrameEncoder.class);
    private byte[] primaryMagic = new byte[] { 'S', 'O', 'F', 'A' };
    private RpcContext context;

    public RpcFrameEncoder(RpcContext context) {
        this.context = context;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        Profiler profiler = new Profiler("ENCODE FRAME");
        profiler.setLogger(logger);
        profiler.start("put msg to context");
        context.put(msg.getSequenceId(), msg);
        ByteBuf header = out.alloc().directBuffer(24);
        header = header.order(ByteOrder.LITTLE_ENDIAN);
        header.writeBytes(primaryMagic);
        header.writeInt(msg.getReqmeta().getSerializedSize());
        header.writeLong(msg.getRequest().getSerializedSize());
        header.writeLong(msg.getReqmeta().getSerializedSize() + msg.getRequest().getSerializedSize());
        out.writeBytes(header);
        profiler.start("encode meta and write");
        msg.getReqmeta().writeTo(new ByteBufOutputStream(out));
        profiler.start("encode request and write");
        msg.getRequest().writeTo(new ByteBufOutputStream(out));
        profiler.stop().log();
        logger.info("buffer size {}", out.writerIndex());
    }

}
