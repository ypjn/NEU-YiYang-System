package neu.YYZX.model;

public class CareProject {
    private String code;
    private String name;
    private String category;
    private String unit;
    private double price;
    private String cycle;
    private int executionCount;
    private String status;
    private String remark;

    public CareProject() {
    }

    public CareProject(String code, String name, String category, String unit,
                       double price, String cycle, int executionCount,
                       String status, String remark) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.cycle = cycle;
        this.executionCount = executionCount;
        this.status = status;
        this.remark = remark;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public int getExecutionCount() { return executionCount; }
    public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
