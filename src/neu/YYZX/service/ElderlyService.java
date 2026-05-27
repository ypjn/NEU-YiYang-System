package neu.YYZX.service;

import neu.YYZX.dao.ElderlyDao;
import neu.YYZX.model.Elderly;

import java.util.List;

/**
 * 老人档案业务服务
 */
public class ElderlyService {
    private final ElderlyDao dao;

    public ElderlyService(ElderlyDao dao) {
        this.dao = dao;
    }

    public List<Elderly> findAll() {
        return dao.findAll();
    }

    public Elderly findById(String id) {
        return dao.findById(id);
    }

    public List<Elderly> findByName(String keyword) {
        return dao.findByName(keyword);
    }

    public List<Elderly> findByNursingLevel(String levelCode) {
        return dao.findByNursingLevel(levelCode);
    }

    public List<Elderly> findActive() {
        return dao.findActive();
    }

    public boolean add(Elderly elderly) {
        if (dao.exists(elderly.getId())) {
            return false;
        }
        if (elderly.getStatus() == null || elderly.getStatus().isEmpty()) {
            elderly.setStatus("在住");
        }
        return dao.insert(elderly);
    }

    public boolean update(Elderly elderly) {
        return dao.update(elderly);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }

    public boolean exists(String id) {
        return dao.exists(id);
    }
}
