package dos.pbrpc.io;


import java.util.concurrent.atomic.AtomicLong;


import dos.pbrpc.RpcContext;
import dos.pbrpc.RpcMessageContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMessageContext>  {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final RpcMessageContext msg) throws Exception {
        msg.getComplete().set(true);
        msg.getDone().run(msg.getResponse());
    }
}
