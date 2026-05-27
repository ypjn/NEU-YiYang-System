package neu.YYZX.model;

/**
 * 管家服务分配实体（健康管家 → 老人 的服务关系）
 */
public class ServiceAssignment {
    private String assignmentId;
    private String employeeId;   // 管家ID
    private String elderlyId;    // 服务对象（老人ID）
    private String serviceType;  // 服务类型
    private String startDate;
    private String endDate;
    private double fee;
    private String status;       // 服务中 / 已到期 / 已续费 / 已撤销
    private String remark;
    private String operatorName; // 操作人姓名（护工或管理员）

    public ServiceAssignment() {
    }

    public ServiceAssignment(String assignmentId, String employeeId, String elderlyId,
                             String serviceType, String startDate, String endDate,
                             double fee, String status, String remark, String operatorName) {
        this.assignmentId = assignmentId;
        this.employeeId = employeeId;
        this.elderlyId = elderlyId;
        this.serviceType = serviceType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
        this.status = status;
        this.remark = remark;
        this.operatorName = operatorName;
    }

    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getElderlyId() { return elderlyId; }
    public void setElderlyId(String elderlyId) { this.elderlyId = elderlyId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
