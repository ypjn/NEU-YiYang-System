package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Bed;

import java.util.ArrayList;
import java.util.List;

/**
 * 床位数据访问层
 */
public class BedDao extends BaseJsonDao<Bed> {

    private static final String FILE_NAME = "beds.json";
    private static final String ID_PREFIX = "BD";
    private static final String ENTITY_TYPE = "bed";

    public BedDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Bed entity) {
        return entity.getBedId();
    }

    @Override
    protected void setEntityId(Bed entity, String id) {
        entity.setBedId(id);
    }

    @Override
    protected TypeReference<List<Bed>> getTypeReference() {
        return new TypeReference<List<Bed>>() {};
    }

    /** 查询指定房间的所有床位 */
    public List<Bed> findByRoomId(String roomId) {
        List<Bed> result = new ArrayList<>();
        for (Bed bed : list) {
            if (bed.getRoomId().equals(roomId)) {
                result.add(bed);
            }
        }
        return result;
    }

    /** 查询空闲床位 */
    public List<Bed> findAvailable() {
        List<Bed> result = new ArrayList<>();
        for (Bed bed : list) {
            if ("空闲".equals(bed.getStatus())) {
                result.add(bed);
            }
        }
        return result;
    }
}
