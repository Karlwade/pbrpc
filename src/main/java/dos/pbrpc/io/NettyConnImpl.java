package dos.pbrpc.io;

import dos.pbrpc.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

/**
 * Created by imotai on 2016/11/3.
 */

public class NettyConnImpl implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(NettyConnImpl.class);
    private ChannelFuture nettyChannel;
    private Options opt;
    private NioEventLoopGroup loop;
    private Bootstrap boot;
    private RpcContext context;

    @Override
    public void connect(Options opt, String host, int port) throws RpcException {
        context = new RpcContext();
        boot = new Bootstrap();
        initEventLoopGroup(opt);
        initChannelHandler();
        try {
            nettyChannel = boot.connect(host, port).sync();
        } catch (InterruptedException e) {
            RpcException ex = new RpcException(-1, e.getMessage());
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        }
    }

    private void initEventLoopGroup(Options opt) {
        RpcThreadFactory factory = new RpcThreadFactory("io-loop");
        loop = new NioEventLoopGroup(opt.getIoThreadCount(),factory);
        boot.group(loop);
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
    }

    private void initChannelHandler() {
        // sofa frame layout
        final LengthFieldBasedFrameDecoder baseDecoder = new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,
                1024 * 1024, 16, 8, 0, 0, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(baseDecoder);
                channel.pipeline().addLast(new FrameDecoder(context));
                channel.pipeline().addLast(new RpcClientHandler());
                channel.pipeline().addLast(new FrameEncoder());
            }
        });
    }

    @Override
    public Channel getChannel() {
        return null;
    }

    @Override
    public void close() {
        logger.info("shutdown connection gracefully");
        if (loop != null) {
            loop.shutdownGracefully();
        }
    }
}
