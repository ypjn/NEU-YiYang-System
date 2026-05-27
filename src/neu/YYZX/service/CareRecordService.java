package neu.YYZX.service;

import neu.YYZX.dao.CareRecordDao;
import neu.YYZX.model.CareRecord;

import java.util.List;

/**
 * 护理记录业务服务
 */
public class CareRecordService {
    private final CareRecordDao dao;

    public CareRecordService(CareRecordDao dao) {
        this.dao = dao;
    }

    public List<CareRecord> findAll() {
        return dao.findAll();
    }

    public CareRecord findById(String id) {
        return dao.findById(id);
    }

    public List<CareRecord> findByElderlyId(String elderlyId) {
        return dao.findByElderlyId(elderlyId);
    }

    public List<CareRecord> findByNurseName(String nurseName) {
        return dao.findByNurseName(nurseName);
    }

    public List<CareRecord> findByProjectCode(String projectCode) {
        return dao.findByProjectCode(projectCode);
    }

    public boolean add(CareRecord record) {
        return dao.insert(record);
    }

    public boolean update(CareRecord record) {
        return dao.update(record);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }
}
