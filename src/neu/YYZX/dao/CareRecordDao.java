package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.CareRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 护理记录数据访问层
 */
public class CareRecordDao extends BaseJsonDao<CareRecord> {

    private static final String FILE_NAME = "care_records.json";
    private static final String ID_PREFIX = "R";
    private static final String ENTITY_TYPE = "care_record";

    public CareRecordDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(CareRecord entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(CareRecord entity, String id) {
        entity.setId(id);
    }

    @Override
    protected TypeReference<List<CareRecord>> getTypeReference() {
        return new TypeReference<List<CareRecord>>() {};
    }

    /** 查询指定老人的护理记录 */
    public List<CareRecord> findByElderlyId(String elderlyId) {
        List<CareRecord> result = new ArrayList<>();
        for (CareRecord r : list) {
            if (r.getElderlyId().equals(elderlyId)) {
                result.add(r);
            }
        }
        return result;
    }

    /** 查询指定护工的护理记录 */
    public List<CareRecord> findByNurseName(String nurseName) {
        List<CareRecord> result = new ArrayList<>();
        for (CareRecord r : list) {
            if (r.getNurseName() != null
                    && r.getNurseName().equalsIgnoreCase(nurseName)) {
                result.add(r);
            }
        }
        return result;
    }

    /** 查询指定项目的护理记录 */
    public List<CareRecord> findByProjectCode(String projectCode) {
        List<CareRecord> result = new ArrayList<>();
        for (CareRecord r : list) {
            if (r.getProjectCode().equals(projectCode)) {
                result.add(r);
            }
        }
        return result;
    }
}
