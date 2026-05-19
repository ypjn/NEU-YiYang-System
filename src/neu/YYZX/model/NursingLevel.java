package neu.YYZX.model;

public class NursingLevel {
    private String code;
    private String name;
    private String description;
    private String frequency;

    public NursingLevel() {
    }

    public NursingLevel(String code, String name, String description, String frequency) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
