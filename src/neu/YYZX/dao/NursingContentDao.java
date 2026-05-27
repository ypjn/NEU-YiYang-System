package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.NursingContent;

import java.util.ArrayList;
import java.util.List;

/**
 * 护理内容数据访问层
 */
public class NursingContentDao extends BaseJsonDao<NursingContent> {

    private static final String FILE_NAME = "nursing_contents.json";
    private static final String ID_PREFIX = "NC";
    private static final String ENTITY_TYPE = "nursing_content";

    public NursingContentDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(NursingContent entity) {
        return entity.getContentId();
    }

    @Override
    protected void setEntityId(NursingContent entity, String id) {
        entity.setContentId(id);
    }

    @Override
    protected TypeReference<List<NursingContent>> getTypeReference() {
        return new TypeReference<List<NursingContent>>() {};
    }

    /** 查询指定护理项目的内容 */
    public List<NursingContent> findByProjectCode(String projectCode) {
        List<NursingContent> result = new ArrayList<>();
        for (NursingContent nc : list) {
            if (nc.getCareProjectCode().equals(projectCode)) {
                result.add(nc);
            }
        }
        return result;
    }

    /** 查询指定护理等级的内容 */
    public List<NursingContent> findByLevelCode(String levelCode) {
        List<NursingContent> result = new ArrayList<>();
        for (NursingContent nc : list) {
            if (nc.getNursingLevelCode().equals(levelCode)) {
                result.add(nc);
            }
        }
        return result;
    }
}
