package neu.YYZX.model;

public class CareRecord {
    private String id;
    private String elderlyId;
    private String projectCode;
    private String executeTime;
    private int quantity;
    private String nurseName;
    private String remark;

    public CareRecord() {
    }

    public CareRecord(String id, String elderlyId, String projectCode,
                      String executeTime, int quantity, String nurseName, String remark) {
        this.id = id;
        this.elderlyId = elderlyId;
        this.projectCode = projectCode;
        this.executeTime = executeTime;
        this.quantity = quantity;
        this.nurseName = nurseName;
        this.remark = remark;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getElderlyId() { return elderlyId; }
    public void setElderlyId(String elderlyId) { this.elderlyId = elderlyId; }
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public String getExecuteTime() { return executeTime; }
    public void setExecuteTime(String executeTime) { this.executeTime = executeTime; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getNurseName() { return nurseName; }
    public void setNurseName(String nurseName) { this.nurseName = nurseName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
