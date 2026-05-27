package neu.YYZX.service;

import neu.YYZX.dao.HealthRecordDao;
import neu.YYZX.model.HealthRecord;

import java.util.List;

/**
 * 健康记录业务服务
 */
public class HealthRecordService {
    private final HealthRecordDao dao;

    public HealthRecordService(HealthRecordDao dao) {
        this.dao = dao;
    }

    public List<HealthRecord> findAll() {
        return dao.findAll();
    }

    public HealthRecord findById(String id) {
        return dao.findById(id);
    }

    public List<HealthRecord> findByCustomerId(String customerId) {
        return dao.findByCustomerId(customerId);
    }

    public List<HealthRecord> findByDateRange(String start, String end) {
        return dao.findByDateRange(start, end);
    }

    public boolean add(HealthRecord record) {
        return dao.insert(record);
    }

    public boolean update(HealthRecord record) {
        return dao.update(record);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }
}
