package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间数据访问层
 */
public class RoomDao extends BaseJsonDao<Room> {

    private static final String FILE_NAME = "rooms.json";
    private static final String ID_PREFIX = "RM";
    private static final String ENTITY_TYPE = "room";

    public RoomDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Room entity) {
        return entity.getRoomId();
    }

    @Override
    protected void setEntityId(Room entity, String id) {
        entity.setRoomId(id);
    }

    @Override
    protected TypeReference<List<Room>> getTypeReference() {
        return new TypeReference<List<Room>>() {};
    }

    /** 查询指定楼栋的所有房间 */
    public List<Room> findByBuildingId(String buildingId) {
        List<Room> result = new ArrayList<>();
        for (Room room : list) {
            if (room.getBuildingId().equals(buildingId)) {
                result.add(room);
            }
        }
        return result;
    }

    /** 查询空闲房间 */
    public List<Room> findAvailable() {
        List<Room> result = new ArrayList<>();
        for (Room room : list) {
            if ("空闲".equals(room.getStatus())) {
                result.add(room);
            }
        }
        return result;
    }
}
