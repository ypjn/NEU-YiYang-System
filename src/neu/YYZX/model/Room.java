package neu.YYZX.model;

/**
 * 房间实体类
 */
public class Room {
    private String roomId;
    private String roomNo;
    private String buildingId;
    private int floor;
    private String roomType;
    private int capacity;
    private double price;
    private String status;

    public Room() {
    }

    public Room(String roomId, String roomNo, String buildingId, int floor,
                String roomType, int capacity, double price, String status) {
        this.roomId = roomId;
        this.roomNo = roomNo;
        this.buildingId = buildingId;
        this.floor = floor;
        this.roomType = roomType;
        this.capacity = capacity;
        this.price = price;
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
