package dos.pbrpc;

/**
 * Created by imotai on 2016/11/3.
 */

public class Options {
    private boolean autoReconnect = true;
    private int ioThreadCount = 1;
    private int workerThreadCount = 10;

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    public void setWorkerThreadCount(int workerThreadCount) {
        this.workerThreadCount = workerThreadCount;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public int getIoThreadCount() {
        return ioThreadCount;
    }

    public void setIoThreadCount(int ioThreadCount) {
        this.ioThreadCount = ioThreadCount;
    }

}
