package bart.model.database;

public class ResultInfo {

    private long size;
    private Long minOid;
    private Long maxOid;

    public ResultInfo(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public Long getMinOid() {
        return minOid;
    }

    public void setMinOid(Long minOid) {
        this.minOid = minOid;
    }

    public Long getMaxOid() {
        return maxOid;
    }

    public void setMaxOid(Long maxOid) {
        this.maxOid = maxOid;
    }

    @Override
    public String toString() {
        return "Result size: " + size + ", minOid=" + minOid + ", maxOid=" + maxOid;
    }

}
