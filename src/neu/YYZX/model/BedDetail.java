package neu.YYZX.model;

/**
 * 床位详情实体类（床位分配记录）
 */
public class BedDetail {
    private String detailId;
    private String bedId;
    private String customerId;
    private String assignDate;
    private String releaseDate;
    private String remark;

    public BedDetail() {
    }

    public BedDetail(String detailId, String bedId, String customerId,
                     String assignDate, String releaseDate, String remark) {
        this.detailId = detailId;
        this.bedId = bedId;
        this.customerId = customerId;
        this.assignDate = assignDate;
        this.releaseDate = releaseDate;
        this.remark = remark;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
