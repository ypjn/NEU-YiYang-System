package neu.YYZX.model;

public class Elderly {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String nursingLevelCode;
    private String roomNo;

    public Elderly() {
    }

    public Elderly(String id, String name, int age, String gender,
                   String nursingLevelCode, String roomNo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.nursingLevelCode = nursingLevelCode;
        this.roomNo = roomNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNursingLevelCode() {
        return nursingLevelCode;
    }

    public void setNursingLevelCode(String nursingLevelCode) {
        this.nursingLevelCode = nursingLevelCode;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }
}
