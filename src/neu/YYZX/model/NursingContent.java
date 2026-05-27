package neu.YYZX.model;

/**
 * 护理内容实体类
 */
public class NursingContent {
    private String contentId;
    private String contentName;
    private String careProjectCode;
    private String nursingLevelCode;
    private String description;
    private String standardDuration;
    private String remark;

    public NursingContent() {
    }

    public NursingContent(String contentId, String contentName, String careProjectCode,
                          String nursingLevelCode, String description,
                          String standardDuration, String remark) {
        this.contentId = contentId;
        this.contentName = contentName;
        this.careProjectCode = careProjectCode;
        this.nursingLevelCode = nursingLevelCode;
        this.description = description;
        this.standardDuration = standardDuration;
        this.remark = remark;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getCareProjectCode() {
        return careProjectCode;
    }

    public void setCareProjectCode(String careProjectCode) {
        this.careProjectCode = careProjectCode;
    }

    public String getNursingLevelCode() {
        return nursingLevelCode;
    }

    public void setNursingLevelCode(String nursingLevelCode) {
        this.nursingLevelCode = nursingLevelCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStandardDuration() {
        return standardDuration;
    }

    public void setStandardDuration(String standardDuration) {
        this.standardDuration = standardDuration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
