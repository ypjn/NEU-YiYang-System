package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Elderly;

import java.util.ArrayList;
import java.util.List;

/**
 * 老人档案数据访问层
 */
public class ElderlyDao extends BaseJsonDao<Elderly> {

    private static final String FILE_NAME = "elders.json";
    private static final String ID_PREFIX = "E";
    private static final String ENTITY_TYPE = "elderly";

    public ElderlyDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Elderly entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(Elderly entity, String id) {
        entity.setId(id);
    }

    @Override
    protected TypeReference<List<Elderly>> getTypeReference() {
        return new TypeReference<List<Elderly>>() {};
    }

    /** 根据姓名关键字搜索老人 */
    public List<Elderly> findByName(String keyword) {
        List<Elderly> result = new ArrayList<>();
        for (Elderly e : list) {
            if (e.getName() != null && e.getName().contains(keyword)) {
                result.add(e);
            }
        }
        return result;
    }

    /** 查询指定护理等级的老人 */
    public List<Elderly> findByNursingLevel(String levelCode) {
        List<Elderly> result = new ArrayList<>();
        for (Elderly e : list) {
            if (e.getNursingLevelCode() != null
                    && e.getNursingLevelCode().equals(levelCode)) {
                result.add(e);
            }
        }
        return result;
    }

    /** 查询指定床位的老人 */
    public Elderly findByBedId(String bedId) {
        for (Elderly e : list) {
            if (e.getBedId() != null && e.getBedId().equals(bedId)) {
                return e;
            }
        }
        return null;
    }

    /** 查询在住老人 */
    public List<Elderly> findActive() {
        List<Elderly> result = new ArrayList<>();
        for (Elderly e : list) {
            if ("在住".equals(e.getStatus())) {
                result.add(e);
            }
        }
        return result;
    }
}
