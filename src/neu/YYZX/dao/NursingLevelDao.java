package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.NursingLevel;

import java.util.List;

/**
 * 护理等级数据访问层
 * 注意：护理等级使用 code 作为唯一标识（手动输入，非自增ID）
 */
public class NursingLevelDao extends BaseJsonDao<NursingLevel> {

    private static final String FILE_NAME = "nursing_levels.json";
    private static final String ID_PREFIX = "NL";
    private static final String ENTITY_TYPE = "nursing_level";

    public NursingLevelDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(NursingLevel entity) {
        return entity.getCode();
    }

    @Override
    protected void setEntityId(NursingLevel entity, String id) {
        entity.setCode(id);
    }

    @Override
    protected TypeReference<List<NursingLevel>> getTypeReference() {
        return new TypeReference<List<NursingLevel>>() {};
    }

    /** 根据code查找护理等级 */
    public NursingLevel findByCode(String code) {
        return findById(code);
    }
}
