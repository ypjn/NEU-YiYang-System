package neu.YYZX.model;

/**
 * 食品实体类
 */
public class Food {
    private String foodId;
    private String foodName;
    private String category;
    private String unit;
    private double price;
    private String nutrition;
    private String remark;

    public Food() {
    }

    public Food(String foodId, String foodName, String category, String unit,
                double price, String nutrition, String remark) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.nutrition = nutrition;
        this.remark = remark;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
