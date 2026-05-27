package neu.YYZX.service;

import neu.YYZX.dao.BuildingDao;
import neu.YYZX.model.Building;

import java.util.List;

/**
 * 楼栋业务服务
 */
public class BuildingService {
    private final BuildingDao dao;
    public BuildingService(BuildingDao dao) { this.dao = dao; }
    public List<Building> findAll() { return dao.findAll(); }
    public Building findById(String id) { return dao.findById(id); }
    public boolean add(Building b) { return dao.insert(b); }
    public boolean update(Building b) { return dao.update(b); }
    public boolean delete(String id) { return dao.delete(id); }
    public boolean exists(String id) { return dao.exists(id); }
}
