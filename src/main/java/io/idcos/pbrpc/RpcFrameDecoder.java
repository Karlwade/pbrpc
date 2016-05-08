package io.idcos.pbrpc;

import java.nio.ByteOrder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import sofa.pbrpc.SofaRpcMeta;
import sofa.pbrpc.SofaRpcMeta.RpcMeta;

/**
 * process the out to protobuf object
 * */
public class RpcFrameDecoder extends ByteToMessageDecoder{
	
	private static final Logger logger = LoggerFactory.getLogger(RpcFrameDecoder.class);
	private byte[] primaryMagic = new byte[]{'S', 'O', 'F','A'};
	private RpcContext context;
	
	public RpcFrameDecoder(RpcContext context) {
		this.context = context;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		Profiler profiler = new Profiler("DECODE FRAME");
		profiler.setLogger(logger);
		ByteBuf buffer = in;
		profiler.start("decode header");
		ByteBuf litBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
		byte[] magic = new byte[4];
		litBuffer.readBytes(magic, 0, 4);
		boolean validateOk = validateMagic(magic);
		if (!validateOk) {
			throw new Exception("magic key is not sofa");
		}
		int metaSize = litBuffer.readInt();
		long dataSize = litBuffer.readLong();
		long totalSize = litBuffer.readLong();
		logger.debug("meta size {} data size {} total size {} of frame", metaSize, dataSize, totalSize);
		profiler.start("decode meta");
		buffer.readerIndex(24);
		RpcMeta resmeta = SofaRpcMeta.RpcMeta.parseFrom(new ByteBufInputStream(buffer, metaSize));
		profiler.start("get msg from context");
		RpcMessage msg = context.get(resmeta.getSequenceId());
		if (msg == null) {
			throw new Exception("no frame with sequence id" + resmeta.getSequenceId());
		}
		msg.setResmeta(resmeta);
		profiler.start("decode response");
		buffer.readerIndex(24 + metaSize);
		if (dataSize > Integer.MAX_VALUE) {
			throw new Exception("too big frame");
		}
		Message response = msg.getResponse().getParserForType().parseFrom(new ByteBufInputStream(buffer, (int) dataSize));
		profiler.start("erase rpc context");
		context.erase(resmeta.getSequenceId());
		profiler.stop().log();
		msg.setResponse(response);
		out.add(msg);
	}
	
	private boolean validateMagic(byte[] headerBuffer) {
		for (int i =0 ; i < 4 ; i ++) {
			if (headerBuffer[i] !=  primaryMagic[i]) {
				return false;
			}
		}
		return true;
	}
}
