package neu.YYZX.model;

/**
 * 退住登记实体类
 */
public class CheckOut {
    private String checkoutId;
    private String customerId;
    private String checkoutType;
    private String checkoutDate;
    private String reason;
    private String approvalStatus;
    private String approvalTime;
    private String approver;
    private String remark;

    public CheckOut() {
    }

    public CheckOut(String checkoutId, String customerId, String checkoutType,
                    String checkoutDate, String reason,
                    String approvalStatus, String approvalTime, String approver,
                    String remark) {
        this.checkoutId = checkoutId;
        this.customerId = customerId;
        this.checkoutType = checkoutType;
        this.checkoutDate = checkoutDate;
        this.reason = reason;
        this.approvalStatus = approvalStatus;
        this.approvalTime = approvalTime;
        this.approver = approver;
        this.remark = remark;
    }

    public String getCheckoutId() { return checkoutId; }
    public void setCheckoutId(String checkoutId) { this.checkoutId = checkoutId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCheckoutType() { return checkoutType; }
    public void setCheckoutType(String checkoutType) { this.checkoutType = checkoutType; }
    public String getCheckoutDate() { return checkoutDate; }
    public void setCheckoutDate(String checkoutDate) { this.checkoutDate = checkoutDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getApprovalTime() { return approvalTime; }
    public void setApprovalTime(String approvalTime) { this.approvalTime = approvalTime; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
