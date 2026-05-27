package neu.YYZX.model;

/**
 * 膳食日历实体类
 */
public class DietCalendar {
    private String calendarId;
    private String calendarDate;
    private String mealType;
    private String foodIds;
    private String remark;

    public DietCalendar() {
    }

    public DietCalendar(String calendarId, String calendarDate, String mealType,
                        String foodIds, String remark) {
        this.calendarId = calendarId;
        this.calendarDate = calendarDate;
        this.mealType = mealType;
        this.foodIds = foodIds;
        this.remark = remark;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFoodIds() {
        return foodIds;
    }

    public void setFoodIds(String foodIds) {
        this.foodIds = foodIds;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
