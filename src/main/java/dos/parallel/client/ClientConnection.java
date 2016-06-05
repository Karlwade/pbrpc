package dos.parallel.client;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dos.parallel.ExchangeDescriptor.Exchange;
import dos.parallel.ExchangeProcessor;
import dos.parallel.FrameDecoder;
import dos.parallel.FrameEncoder;
import dos.parallel.IDoneCallback;
import dos.parallel.ITaskProcessor;
import dos.pbrpc.NettyConnection;
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

public class ClientConnection {
    private static final Logger logger = LoggerFactory.getLogger(NettyConnection.class);
    private ChannelFuture channel = null;
    private EventLoopGroup workerGroup = null;
    private IDoneCallback doneCallback = null;
    private ITaskProcessor taskProcessor = null;
    public ClientConnection(IDoneCallback doneCallback, ITaskProcessor taskProcessor) {
        this.taskProcessor = taskProcessor;
        this.doneCallback = doneCallback;
    }
    
    public boolean build(String host, int port) {
        workerGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap(); 
        b.group(workerGroup); 
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast("framedecoder",
                        new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024 * 1024, 4, 8, 0, 0, true));
                channel.pipeline().addLast("msgdecoder", new FrameDecoder());
                channel.pipeline().addLast("exchange", new ExchangeProcessor(doneCallback, taskProcessor));
                channel.pipeline().addLast("msgencoder", new FrameEncoder());
            }
        });
        
        try {
            channel = b.connect(host, port).sync();
            logger.info("connect to {} on port {} successfully", host, port);
            return true;
        } catch (InterruptedException e) {
            logger.error("interrupted", e);
            return false;
        }
    }
    
    public void submit(Exchange exchange) {
        channel.channel().pipeline().writeAndFlush(exchange);
    }
    
    public void close() {
        this.workerGroup.shutdownGracefully();
    }
}
