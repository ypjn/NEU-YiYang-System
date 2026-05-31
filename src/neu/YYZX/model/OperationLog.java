package neu.YYZX.model;

/**
 * 操作日志实体
 */
public class OperationLog {
    private String logId;
    private String operatorName;
    private String operatorRole;
    private String action;
    private String target;
    private String detail;
    private String time;
    private String reversibleData;
    private boolean reverted;

    public OperationLog() {}

    public OperationLog(String logId, String operatorName, String operatorRole,
                        String action, String target, String detail, String time) {
        this.logId = logId;
        this.operatorName = operatorName;
        this.operatorRole = operatorRole;
        this.action = action;
        this.target = target;
        this.detail = detail;
        this.time = time;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getOperatorRole() { return operatorRole; }
    public void setOperatorRole(String operatorRole) { this.operatorRole = operatorRole; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getReversibleData() { return reversibleData; }
    public void setReversibleData(String reversibleData) { this.reversibleData = reversibleData; }
    public boolean isReverted() { return reverted; }
    public void setReverted(boolean reverted) { this.reverted = reverted; }
}
