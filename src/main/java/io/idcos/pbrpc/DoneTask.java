package io.idcos.pbrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoneTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SyncRpcChannel.class);
    private RpcMessage msg;

    public DoneTask(RpcMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        logger.debug("response with seq {} come back", msg.getSequenceId());
        msg.getComplete().set(true);
        msg.getDone().run(msg.getResponse());
    }

}
