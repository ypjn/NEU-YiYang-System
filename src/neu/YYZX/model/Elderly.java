package neu.YYZX.model;

/**
 * 老人档案实体类（客户）
 */
public class Elderly {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String idCard;
    private String bloodType;
    private String birthDate;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String familyMember;
    private String checkInDate;
    private String contractEndDate;
    private String bedId;
    private String nursingLevelCode;
    private String roomNo;
    private String buildingId;
    private String status;

    public Elderly() {
    }

    public Elderly(String id, String name, int age, String gender,
                   String nursingLevelCode, String roomNo) {
        this(id, name, age, gender, null, null, null, null, null,
                null, null, null, null, null, null,
                nursingLevelCode, roomNo, null, "在住");
    }

    public Elderly(String id, String name, int age, String gender,
                   String idCard, String bloodType, String birthDate,
                   String phone, String address,
                   String emergencyContact, String emergencyPhone, String familyMember,
                   String checkInDate, String contractEndDate, String bedId,
                   String nursingLevelCode, String roomNo, String buildingId, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.idCard = idCard;
        this.bloodType = bloodType;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.familyMember = familyMember;
        this.checkInDate = checkInDate;
        this.contractEndDate = contractEndDate;
        this.bedId = bedId;
        this.nursingLevelCode = nursingLevelCode;
        this.roomNo = roomNo;
        this.buildingId = buildingId;
        this.status = status;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getFamilyMember() { return familyMember; }
    public void setFamilyMember(String familyMember) { this.familyMember = familyMember; }

    public String getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(String contractEndDate) { this.contractEndDate = contractEndDate; }

    public String getBuildingId() { return buildingId; }
    public void setBuildingId(String buildingId) { this.buildingId = buildingId; }
}
