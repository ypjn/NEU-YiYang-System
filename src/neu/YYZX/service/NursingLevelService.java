package neu.YYZX.service;

import neu.YYZX.dao.NursingLevelDao;
import neu.YYZX.model.NursingLevel;

import java.util.List;

/**
 * 护理等级业务服务
 */
public class NursingLevelService {
    private final NursingLevelDao dao;

    public NursingLevelService(NursingLevelDao dao) {
        this.dao = dao;
    }

    public List<NursingLevel> findAll() {
        return dao.findAll();
    }

    public NursingLevel findByCode(String code) {
        return dao.findByCode(code);
    }

    public boolean add(NursingLevel level) {
        if (dao.exists(level.getCode())) {
            return false;
        }
        return dao.insert(level);
    }

    public boolean update(NursingLevel level) {
        return dao.update(level);
    }

    public boolean delete(String code) {
        return dao.delete(code);
    }

    public boolean exists(String code) {
        return dao.exists(code);
    }
}
