package neu.YYZX.service;

import neu.YYZX.dao.BedDao;
import neu.YYZX.model.Bed;

import java.util.List;

/**
 * 床位业务服务
 */
public class BedService {
    private final BedDao dao;

    public BedService(BedDao dao) {
        this.dao = dao;
    }

    public List<Bed> findAll() {
        return dao.findAll();
    }

    public Bed findById(String id) {
        return dao.findById(id);
    }

    public List<Bed> findByRoomId(String roomId) {
        return dao.findByRoomId(roomId);
    }

    public List<Bed> findAvailable() {
        return dao.findAvailable();
    }

    public boolean add(Bed bed) {
        if (bed.getStatus() == null || bed.getStatus().isEmpty()) {
            bed.setStatus("空闲");
        }
        return dao.insert(bed);
    }

    public boolean update(Bed bed) {
        return dao.update(bed);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }

    public boolean exists(String id) {
        return dao.exists(id);
    }
}
