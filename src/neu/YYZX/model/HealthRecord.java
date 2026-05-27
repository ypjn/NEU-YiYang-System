package neu.YYZX.model;

/**
 * 健康记录实体类
 */
public class HealthRecord {
    private String healthId;
    private String customerId;
    private String recordDate;
    private String bloodPressure;
    private String heartRate;
    private String bloodSugar;
    private String weight;
    private String temperature;
    private String remark;

    public HealthRecord() {
    }

    public HealthRecord(String healthId, String customerId, String recordDate,
                        String bloodPressure, String heartRate, String bloodSugar,
                        String weight, String temperature, String remark) {
        this.healthId = healthId;
        this.customerId = customerId;
        this.recordDate = recordDate;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.bloodSugar = bloodSugar;
        this.weight = weight;
        this.temperature = temperature;
        this.remark = remark;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
