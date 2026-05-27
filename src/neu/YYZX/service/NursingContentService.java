package neu.YYZX.service;

import neu.YYZX.dao.NursingContentDao;
import neu.YYZX.model.NursingContent;

import java.util.List;

/**
 * 护理内容业务服务
 */
public class NursingContentService {
    private final NursingContentDao dao;
    public NursingContentService(NursingContentDao dao) { this.dao = dao; }
    public List<NursingContent> findAll() { return dao.findAll(); }
    public NursingContent findById(String id) { return dao.findById(id); }
    public List<NursingContent> findByProjectCode(String code) { return dao.findByProjectCode(code); }
    public List<NursingContent> findByLevelCode(String code) { return dao.findByLevelCode(code); }
    public boolean add(NursingContent nc) { return dao.insert(nc); }
    public boolean update(NursingContent nc) { return dao.update(nc); }
    public boolean delete(String id) { return dao.delete(id); }
}
