package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.HealthRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 健康记录数据访问层
 */
public class HealthRecordDao extends BaseJsonDao<HealthRecord> {

    private static final String FILE_NAME = "health_records.json";
    private static final String ID_PREFIX = "HR";
    private static final String ENTITY_TYPE = "health_record";

    public HealthRecordDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(HealthRecord entity) {
        return entity.getHealthId();
    }

    @Override
    protected void setEntityId(HealthRecord entity, String id) {
        entity.setHealthId(id);
    }

    @Override
    protected TypeReference<List<HealthRecord>> getTypeReference() {
        return new TypeReference<List<HealthRecord>>() {};
    }

    /** 查询指定客户的健康记录 */
    public List<HealthRecord> findByCustomerId(String customerId) {
        List<HealthRecord> result = new ArrayList<>();
        for (HealthRecord hr : list) {
            if (hr.getCustomerId().equals(customerId)) {
                result.add(hr);
            }
        }
        return result;
    }

    /** 按日期范围查询健康记录 */
    public List<HealthRecord> findByDateRange(String startDate, String endDate) {
        List<HealthRecord> result = new ArrayList<>();
        for (HealthRecord hr : list) {
            if (hr.getRecordDate().compareTo(startDate) >= 0
                    && hr.getRecordDate().compareTo(endDate) <= 0) {
                result.add(hr);
            }
        }
        return result;
    }
}
