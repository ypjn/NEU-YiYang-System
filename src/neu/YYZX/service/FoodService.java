package neu.YYZX.service;

import neu.YYZX.dao.FoodDao;
import neu.YYZX.model.Food;

import java.util.List;

/**
 * 食品业务服务
 */
public class FoodService {
    private final FoodDao dao;

    public FoodService(FoodDao dao) {
        this.dao = dao;
    }

    public List<Food> findAll() {
        return dao.findAll();
    }

    public Food findById(String id) {
        return dao.findById(id);
    }

    public List<Food> findByCategory(String category) {
        return dao.findByCategory(category);
    }

    public List<Food> findByName(String keyword) {
        return dao.findByName(keyword);
    }

    public boolean add(Food food) {
        return dao.insert(food);
    }

    public boolean update(Food food) {
        return dao.update(food);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }

    public boolean exists(String id) {
        return dao.exists(id);
    }
}
