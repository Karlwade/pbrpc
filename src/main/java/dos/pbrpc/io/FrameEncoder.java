package dos.pbrpc.io;

import java.nio.ByteOrder;

import dos.pbrpc.RpcMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FrameEncoder extends MessageToByteEncoder<RpcMessageContext> {
    private static final Logger logger = LoggerFactory.getLogger(FrameEncoder.class);
    private byte[] primaryMagic = new byte[] { 'S', 'O', 'F', 'A' };
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessageContext msg, ByteBuf out) throws Exception {
        ByteBuf header = out.alloc().directBuffer(24);
        header = header.order(ByteOrder.LITTLE_ENDIAN);
        header.writeBytes(primaryMagic);
        header.writeInt(msg.getRequestMeta().getSerializedSize());
        header.writeLong(msg.getRequest().getSerializedSize());
        header.writeLong(msg.getRequestMeta().getSerializedSize() + msg.getRequest().getSerializedSize());
        out.writeBytes(header);
        msg.getRequestMeta().writeTo(new ByteBufOutputStream(out));
        msg.getRequest().writeTo(new ByteBufOutputStream(out));
    }

}
