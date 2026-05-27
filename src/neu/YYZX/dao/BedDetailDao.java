package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.BedDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 床位详情数据访问层
 */
public class BedDetailDao extends BaseJsonDao<BedDetail> {

    private static final String FILE_NAME = "bed_details.json";
    private static final String ID_PREFIX = "BDD";
    private static final String ENTITY_TYPE = "bed_detail";

    public BedDetailDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(BedDetail entity) {
        return entity.getDetailId();
    }

    @Override
    protected void setEntityId(BedDetail entity, String id) {
        entity.setDetailId(id);
    }

    @Override
    protected TypeReference<List<BedDetail>> getTypeReference() {
        return new TypeReference<List<BedDetail>>() {};
    }

    /** 查询指定床位的分配记录 */
    public List<BedDetail> findByBedId(String bedId) {
        List<BedDetail> result = new ArrayList<>();
        for (BedDetail bd : list) {
            if (bd.getBedId().equals(bedId)) {
                result.add(bd);
            }
        }
        return result;
    }

    /** 查询指定客户的床位分配记录 */
    public List<BedDetail> findByCustomerId(String customerId) {
        List<BedDetail> result = new ArrayList<>();
        for (BedDetail bd : list) {
            if (bd.getCustomerId().equals(customerId)) {
                result.add(bd);
            }
        }
        return result;
    }
}
