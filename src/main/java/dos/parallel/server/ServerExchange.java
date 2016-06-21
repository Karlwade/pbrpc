package dos.parallel.server;

import java.nio.ByteOrder;

import dos.parallel.ExchangeProcessor;
import dos.parallel.FrameDecoder;
import dos.parallel.FrameEncoder;
import dos.parallel.IDoneCallback;
import dos.parallel.ITaskProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class ServerExchange {

    private IDoneCallback doneCallback;
    private ITaskProcessor taskProcessor = null; 
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    public ServerExchange(IDoneCallback doneCallback,
            ITaskProcessor taskProcessor) {
        this.doneCallback = doneCallback;
        this.taskProcessor = taskProcessor;
    }
    
    public boolean build(String host, int port) {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(50);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel channel) throws Exception {
                     channel.pipeline().addLast("framedecoder",
                                        new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,
                                                1024 * 1024, 4, 8, 0, 0, true));
                     channel.pipeline().addLast("msgdecoder", new FrameDecoder());
                     channel.pipeline().addLast("exchange", new ExchangeProcessor(doneCallback,taskProcessor));
                     channel.pipeline().addLast("msgencoder", new FrameEncoder());
                 }
             });
            b.bind(host, port).sync();
        } catch (Exception e) {
            
        }
        return false;
    }
    
    public void close() {
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
    }
}
