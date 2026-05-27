package neu.YYZX.model;

/**
 * 楼栋实体类
 */
public class Building {
    private String buildingId;
    private String buildingName;
    private int floorCount;
    private String description;

    public Building() {
    }

    public Building(String buildingId, String buildingName, int floorCount, String description) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.floorCount = floorCount;
        this.description = description;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
