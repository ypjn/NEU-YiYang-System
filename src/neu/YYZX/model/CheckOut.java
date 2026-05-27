package neu.YYZX.model;

/**
 * 退住登记实体类
 */
public class CheckOut {
    private String checkoutId;
    private String customerId;
    private String checkoutDate;
    private String reason;
    private String remark;

    public CheckOut() {
    }

    public CheckOut(String checkoutId, String customerId, String checkoutDate,
                    String reason, String remark) {
        this.checkoutId = checkoutId;
        this.customerId = customerId;
        this.checkoutDate = checkoutDate;
        this.reason = reason;
        this.remark = remark;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
