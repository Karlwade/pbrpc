package dos.pbrpc.io;

import dos.pbrpc.RpcMessageContext;
import io.netty.channel.ChannelFuture;

/**
 * Created by imotai on 2016/11/4.
 */
public class NettyChannelImpl  implements Channel {
    private ChannelFuture nettyChannel;
    public NettyChannelImpl(ChannelFuture channelFuture) {
        this.nettyChannel = channelFuture;
    }
    public void sendMessage(RpcMessageContext m) {
        nettyChannel.channel().pipeline().writeAndFlush(m);
    }
}
