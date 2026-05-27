package neu.YYZX.model;

/**
 * 用户实体类
 */
public class User {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_NURSE = "nurse";

    private String userId;
    private String username;
    private String password;
    private String role;
    private String realName;
    private String phone;
    private String securityQuestion;
    private String securityAnswer;
    private String createTime;

    public User() {
    }

    /** 兼容旧版3参数构造器 */
    public User(String username, String password, String role) {
        this(null, username, password, role, null, null, null, null, null);
    }

    public User(String userId, String username, String password, String role,
                String realName, String phone, String securityQuestion, String securityAnswer,
                String createTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.realName = realName;
        this.phone = phone;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
