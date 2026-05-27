package neu.YYZX.model;

/**
 * 饮食喜好实体类
 */
public class DietPreference {
    private String preferenceId;
    private String customerId;
    private String preferenceType;
    private String description;
    private String allergies;
    private String taboos;
    private String remark;

    public DietPreference() {
    }

    public DietPreference(String preferenceId, String customerId, String preferenceType,
                          String description, String allergies, String taboos, String remark) {
        this.preferenceId = preferenceId;
        this.customerId = customerId;
        this.preferenceType = preferenceType;
        this.description = description;
        this.allergies = allergies;
        this.taboos = taboos;
        this.remark = remark;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPreferenceType() {
        return preferenceType;
    }

    public void setPreferenceType(String preferenceType) {
        this.preferenceType = preferenceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getTaboos() {
        return taboos;
    }

    public void setTaboos(String taboos) {
        this.taboos = taboos;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
