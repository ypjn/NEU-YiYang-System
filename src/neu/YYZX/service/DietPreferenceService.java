package neu.YYZX.service;

import neu.YYZX.dao.DietPreferenceDao;
import neu.YYZX.model.DietPreference;

import java.util.List;

/**
 * 饮食喜好业务服务
 */
public class DietPreferenceService {
    private final DietPreferenceDao dao;
    public DietPreferenceService(DietPreferenceDao dao) { this.dao = dao; }
    public List<DietPreference> findAll() { return dao.findAll(); }
    public DietPreference findById(String id) { return dao.findById(id); }
    public DietPreference findByCustomerId(String customerId) { return dao.findByCustomerId(customerId); }
    public boolean add(DietPreference dp) { return dao.insert(dp); }
    public boolean update(DietPreference dp) { return dao.update(dp); }
    public boolean delete(String id) { return dao.delete(id); }
}
