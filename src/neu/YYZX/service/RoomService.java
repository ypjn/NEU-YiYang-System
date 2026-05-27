package neu.YYZX.service;

import neu.YYZX.dao.RoomDao;
import neu.YYZX.model.Room;

import java.util.List;

/**
 * 房间业务服务
 */
public class RoomService {
    private final RoomDao dao;

    public RoomService(RoomDao dao) {
        this.dao = dao;
    }

    public List<Room> findAll() {
        return dao.findAll();
    }

    public Room findById(String id) {
        return dao.findById(id);
    }

    public List<Room> findByBuildingId(String buildingId) {
        return dao.findByBuildingId(buildingId);
    }

    public List<Room> findAvailable() {
        return dao.findAvailable();
    }

    public boolean add(Room room) {
        if (room.getStatus() == null || room.getStatus().isEmpty()) {
            room.setStatus("空闲");
        }
        return dao.insert(room);
    }

    public boolean update(Room room) {
        return dao.update(room);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }

    public boolean exists(String id) {
        return dao.exists(id);
    }
}
