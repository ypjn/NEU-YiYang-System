package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.DietPreference;

import java.util.List;

/**
 * 饮食喜好数据访问层
 */
public class DietPreferenceDao extends BaseJsonDao<DietPreference> {

    private static final String FILE_NAME = "diet_preferences.json";
    private static final String ID_PREFIX = "DP";
    private static final String ENTITY_TYPE = "diet_preference";

    public DietPreferenceDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(DietPreference entity) {
        return entity.getPreferenceId();
    }

    @Override
    protected void setEntityId(DietPreference entity, String id) {
        entity.setPreferenceId(id);
    }

    @Override
    protected TypeReference<List<DietPreference>> getTypeReference() {
        return new TypeReference<List<DietPreference>>() {};
    }

    /** 根据客户ID查找饮食喜好 */
    public DietPreference findByCustomerId(String customerId) {
        for (DietPreference dp : list) {
            if (dp.getCustomerId().equals(customerId)) {
                return dp;
            }
        }
        return null;
    }
}
