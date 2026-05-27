package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * 食品数据访问层
 */
public class FoodDao extends BaseJsonDao<Food> {

    private static final String FILE_NAME = "foods.json";
    private static final String ID_PREFIX = "F";
    private static final String ENTITY_TYPE = "food";

    public FoodDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Food entity) {
        return entity.getFoodId();
    }

    @Override
    protected void setEntityId(Food entity, String id) {
        entity.setFoodId(id);
    }

    @Override
    protected TypeReference<List<Food>> getTypeReference() {
        return new TypeReference<List<Food>>() {};
    }

    /** 按类别查询食品 */
    public List<Food> findByCategory(String category) {
        List<Food> result = new ArrayList<>();
        for (Food food : list) {
            if (food.getCategory().equals(category)) {
                result.add(food);
            }
        }
        return result;
    }

    /** 根据名称关键字搜索食品 */
    public List<Food> findByName(String keyword) {
        List<Food> result = new ArrayList<>();
        for (Food food : list) {
            if (food.getFoodName() != null
                    && food.getFoodName().contains(keyword)) {
                result.add(food);
            }
        }
        return result;
    }
}
