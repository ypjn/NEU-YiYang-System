package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.OutRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * 外出登记数据访问层
 */
public class OutRegistrationDao extends BaseJsonDao<OutRegistration> {

    private static final String FILE_NAME = "out_registrations.json";
    private static final String ID_PREFIX = "OUT";
    private static final String ENTITY_TYPE = "out_registration";

    public OutRegistrationDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(OutRegistration entity) {
        return entity.getOutId();
    }

    @Override
    protected void setEntityId(OutRegistration entity, String id) {
        entity.setOutId(id);
    }

    @Override
    protected TypeReference<List<OutRegistration>> getTypeReference() {
        return new TypeReference<List<OutRegistration>>() {};
    }

    /** 查询指定客户的外出记录 */
    public List<OutRegistration> findByCustomerId(String customerId) {
        List<OutRegistration> result = new ArrayList<>();
        for (OutRegistration o : list) {
            if (o.getCustomerId().equals(customerId)) {
                result.add(o);
            }
        }
        return result;
    }

    /** 查询指定状态的外出记录 */
    public List<OutRegistration> findByStatus(String status) {
        List<OutRegistration> result = new ArrayList<>();
        for (OutRegistration o : list) {
            if (o.getStatus().equals(status)) {
                result.add(o);
            }
        }
        return result;
    }
}
