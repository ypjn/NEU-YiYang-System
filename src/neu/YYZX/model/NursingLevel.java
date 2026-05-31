package neu.YYZX.model;

public class NursingLevel {
    private String code;
    private String name;
    private String description;
    private String frequency;
    private String status;

    public NursingLevel() {
    }

    public NursingLevel(String code, String name, String description, String frequency, String status) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.status = status;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
