package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.CareProject;

import java.util.ArrayList;
import java.util.List;

/**
 * 护理项目数据访问层
 * 注意：护理项目使用 code 作为唯一标识（手动输入，非自增ID）
 */
public class CareProjectDao extends BaseJsonDao<CareProject> {

    private static final String FILE_NAME = "care_projects.json";
    private static final String ID_PREFIX = "CP";
    private static final String ENTITY_TYPE = "care_project";

    public CareProjectDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(CareProject entity) {
        return entity.getCode();
    }

    @Override
    protected void setEntityId(CareProject entity, String id) {
        entity.setCode(id);
    }

    @Override
    protected TypeReference<List<CareProject>> getTypeReference() {
        return new TypeReference<List<CareProject>>() {};
    }

    /** 根据code查找护理项目 */
    public CareProject findByCode(String code) {
        return findById(code);
    }

    /** 查找适用于指定护理等级的项目 */
    public List<CareProject> findApplicable(String nursingLevelCode) {
        List<CareProject> result = new ArrayList<>();
        for (CareProject p : list) {
            String remark = p.getRemark();
            if (remark != null && (remark.contains("所有护理级")
                    || remark.contains(nursingLevelCode))) {
                result.add(p);
            }
        }
        return result;
    }
}
