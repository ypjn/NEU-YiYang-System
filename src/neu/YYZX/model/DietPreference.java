package neu.YYZX.model;

/**
 * 饮食喜好实体类
 */
public class DietPreference {
    private String preferenceId;
    private String customerId;
    private String taste;
    private String dietaryAdvice;
    private String allergies;
    private String taboos;
    private String remark;

    public DietPreference() {
    }

    public DietPreference(String preferenceId, String customerId, String taste,
                          String dietaryAdvice, String allergies, String taboos, String remark) {
        this.preferenceId = preferenceId;
        this.customerId = customerId;
        this.taste = taste;
        this.dietaryAdvice = dietaryAdvice;
        this.allergies = allergies;
        this.taboos = taboos;
        this.remark = remark;
    }

    public String getPreferenceId() { return preferenceId; }
    public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }
    public String getDietaryAdvice() { return dietaryAdvice; }
    public void setDietaryAdvice(String dietaryAdvice) { this.dietaryAdvice = dietaryAdvice; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getTaboos() { return taboos; }
    public void setTaboos(String taboos) { this.taboos = taboos; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
