package neu.YYZX.service;

import neu.YYZX.dao.CareProjectDao;
import neu.YYZX.model.CareProject;

import java.util.List;

/**
 * 护理项目业务服务
 */
public class CareProjectService {
    private final CareProjectDao dao;

    public CareProjectService(CareProjectDao dao) {
        this.dao = dao;
    }

    public List<CareProject> findAll() {
        return dao.findAll();
    }

    public CareProject findByCode(String code) {
        return dao.findByCode(code);
    }

    public boolean add(CareProject project) {
        if (dao.exists(project.getCode())) {
            return false;
        }
        return dao.insert(project);
    }

    public boolean update(CareProject project) {
        return dao.update(project);
    }

    public boolean delete(String code) {
        return dao.delete(code);
    }

    public boolean exists(String code) {
        return dao.exists(code);
    }

    /** 查询适用于指定护理等级的项目 */
    public List<CareProject> findApplicable(String nursingLevelCode) {
        return dao.findApplicable(nursingLevelCode);
    }
}
