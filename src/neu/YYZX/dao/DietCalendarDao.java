package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.DietCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * 膳食日历数据访问层
 */
public class DietCalendarDao extends BaseJsonDao<DietCalendar> {

    private static final String FILE_NAME = "diet_calendars.json";
    private static final String ID_PREFIX = "DC";
    private static final String ENTITY_TYPE = "diet_calendar";

    public DietCalendarDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(DietCalendar entity) {
        return entity.getCalendarId();
    }

    @Override
    protected void setEntityId(DietCalendar entity, String id) {
        entity.setCalendarId(id);
    }

    @Override
    protected TypeReference<List<DietCalendar>> getTypeReference() {
        return new TypeReference<List<DietCalendar>>() {};
    }

    /** 按日期查询膳食 */
    public List<DietCalendar> findByDate(String date) {
        List<DietCalendar> result = new ArrayList<>();
        for (DietCalendar dc : list) {
            if (dc.getCalendarDate().equals(date)) {
                result.add(dc);
            }
        }
        return result;
    }

    /** 按日期和餐次查询 */
    public DietCalendar findByDateAndMealType(String date, String mealType) {
        for (DietCalendar dc : list) {
            if (dc.getCalendarDate().equals(date)
                    && dc.getMealType().equals(mealType)) {
                return dc;
            }
        }
        return null;
    }
}
