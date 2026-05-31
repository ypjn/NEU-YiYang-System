package neu.YYZX.model;

/**
 * 客户护理项目订阅（客户购买的护理服务）
 */
public class CustomerCareProject {
    private String id;
    private String customerId;
    private String projectCode;
    private int quantity;
    private String purchaseDate;
    private String expireDate;

    public CustomerCareProject() {
    }

    public CustomerCareProject(String id, String customerId, String projectCode,
                               int quantity, String purchaseDate, String expireDate) {
        this.id = id;
        this.customerId = customerId;
        this.projectCode = projectCode;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getExpireDate() { return expireDate; }
    public void setExpireDate(String expireDate) { this.expireDate = expireDate; }
}
