package neu.YYZX.service;

import neu.YYZX.dao.DietCalendarDao;
import neu.YYZX.model.DietCalendar;

import java.util.List;

/**
 * 膳食日历业务服务
 */
public class DietCalendarService {
    private final DietCalendarDao dao;

    public DietCalendarService(DietCalendarDao dao) {
        this.dao = dao;
    }

    public List<DietCalendar> findAll() {
        return dao.findAll();
    }

    public DietCalendar findById(String id) {
        return dao.findById(id);
    }

    public List<DietCalendar> findByDate(String date) {
        return dao.findByDate(date);
    }

    public DietCalendar findByDateAndMealType(String date, String mealType) {
        return dao.findByDateAndMealType(date, mealType);
    }

    public boolean add(DietCalendar dc) {
        return dao.insert(dc);
    }

    public boolean update(DietCalendar dc) {
        return dao.update(dc);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }
}
