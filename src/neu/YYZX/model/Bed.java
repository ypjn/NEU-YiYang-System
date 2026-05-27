package neu.YYZX.model;

/**
 * 床位实体类
 */
public class Bed {
    private String bedId;
    private String bedNo;
    private String roomId;
    private String status;

    public Bed() {
    }

    public Bed(String bedId, String bedNo, String roomId, String status) {
        this.bedId = bedId;
        this.bedNo = bedNo;
        this.roomId = roomId;
        this.status = status;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getBedNo() {
        return bedNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
