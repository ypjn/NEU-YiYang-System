package neu.YYZX.model;

/**
 * 员工实体类（与系统登录账号 User 区分）
 */
public class Employee {
    private String employeeId;
    private String name;
    private String gender;
    private String position;
    private String phone;
    private String idCard;
    private String hireDate;
    private double salary;
    private String status;
    private String remark;

    public Employee() {
    }

    public Employee(String employeeId, String name, String gender, String position,
                    String phone, String idCard, String hireDate, double salary,
                    String status, String remark) {
        this.employeeId = employeeId;
        this.name = name;
        this.gender = gender;
        this.position = position;
        this.phone = phone;
        this.idCard = idCard;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = status;
        this.remark = remark;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
