package dos.table;

class TableInfo {

    private String tableName;
    private String path;
    private String serverAddr;
    private String startKey;
    private String endKey;
    private long dataSize;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getStartKey() {
        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    public String getEndKey() {
        return endKey;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

}
