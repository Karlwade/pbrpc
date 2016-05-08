package io.idcos.pbrpc;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyConnection implements IRpcConnection {
	private static final Logger logger = LoggerFactory.getLogger(NettyConnection.class);
	private ChannelFuture channel = null;
	private EventLoopGroup workerGroup = null;
	private final RpcContext pbrpcContext = new RpcContext();
	
	public boolean start(String host, int port) {
		workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap(); // (1)
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast("framedecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024 * 1024, 16, 8, 0, 0, true));
                channel.pipeline().addLast("msgdecoder", new RpcFrameDecoder(pbrpcContext));
                channel.pipeline().addLast("rpcin", new RpcClientHandler());
                channel.pipeline().addLast("msgencoder", new RpcFrameEncoder(pbrpcContext));
            }
        });
        
        // Start the client.
        try {
			channel= b.connect(host, port).sync();
			logger.info("connect to {} on port {} successfully", host, port);
			return true;
		} catch (InterruptedException e) {
			logger.error("interrupted", e);
			return false;
		} 
	}
	
	@Override
	public void sendMsg(RpcMessage message) {
		channel.channel().pipeline().writeAndFlush(message);
	}

	@Override
	public void close() {
		this.channel.channel().close();
	}
}
