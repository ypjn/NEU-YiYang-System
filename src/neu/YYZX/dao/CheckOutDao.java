package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.CheckOut;

import java.util.ArrayList;
import java.util.List;

/**
 * 退住登记数据访问层
 */
public class CheckOutDao extends BaseJsonDao<CheckOut> {

    private static final String FILE_NAME = "check_outs.json";
    private static final String ID_PREFIX = "CO";
    private static final String ENTITY_TYPE = "check_out";

    public CheckOutDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(CheckOut entity) {
        return entity.getCheckoutId();
    }

    @Override
    protected void setEntityId(CheckOut entity, String id) {
        entity.setCheckoutId(id);
    }

    @Override
    protected TypeReference<List<CheckOut>> getTypeReference() {
        return new TypeReference<List<CheckOut>>() {};
    }

    /** 查询指定客户的退住记录 */
    public List<CheckOut> findByCustomerId(String customerId) {
        List<CheckOut> result = new ArrayList<>();
        for (CheckOut co : list) {
            if (co.getCustomerId().equals(customerId)) {
                result.add(co);
            }
        }
        return result;
    }
}
