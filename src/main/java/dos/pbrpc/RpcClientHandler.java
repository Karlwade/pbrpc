package dos.pbrpc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        executorService.execute(new DoneTask(msg));
    }
}
