package neu.YYZX.model;

/**
 * 外出登记实体类
 */
public class OutRegistration {
    private String outId;
    private String customerId;
    private String outTime;
    private String expectedReturnTime;
    private String actualReturnTime;
    private String companion;
    private String reason;
    private String status;
    private String approvalStatus;
    private String approvalTime;
    private String approver;

    public OutRegistration() {
    }

    public OutRegistration(String outId, String customerId, String outTime,
                           String expectedReturnTime, String actualReturnTime,
                           String companion, String reason, String status,
                           String approvalStatus, String approvalTime, String approver) {
        this.outId = outId;
        this.customerId = customerId;
        this.outTime = outTime;
        this.expectedReturnTime = expectedReturnTime;
        this.actualReturnTime = actualReturnTime;
        this.companion = companion;
        this.reason = reason;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.approvalTime = approvalTime;
        this.approver = approver;
    }

    public String getOutId() { return outId; }
    public void setOutId(String outId) { this.outId = outId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getOutTime() { return outTime; }
    public void setOutTime(String outTime) { this.outTime = outTime; }
    public String getExpectedReturnTime() { return expectedReturnTime; }
    public void setExpectedReturnTime(String expectedReturnTime) { this.expectedReturnTime = expectedReturnTime; }
    public String getActualReturnTime() { return actualReturnTime; }
    public void setActualReturnTime(String actualReturnTime) { this.actualReturnTime = actualReturnTime; }
    public String getCompanion() { return companion; }
    public void setCompanion(String companion) { this.companion = companion; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getApprovalTime() { return approvalTime; }
    public void setApprovalTime(String approvalTime) { this.approvalTime = approvalTime; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
}
