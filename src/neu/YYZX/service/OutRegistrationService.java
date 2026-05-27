package neu.YYZX.service;

import neu.YYZX.dao.OutRegistrationDao;
import neu.YYZX.model.OutRegistration;

import java.util.List;

/**
 * 外出登记业务服务
 */
public class OutRegistrationService {
    private final OutRegistrationDao dao;

    public OutRegistrationService(OutRegistrationDao dao) {
        this.dao = dao;
    }

    public List<OutRegistration> findAll() {
        return dao.findAll();
    }

    public OutRegistration findById(String id) {
        return dao.findById(id);
    }

    public List<OutRegistration> findByCustomerId(String customerId) {
        return dao.findByCustomerId(customerId);
    }

    public List<OutRegistration> findByStatus(String status) {
        return dao.findByStatus(status);
    }

    public boolean add(OutRegistration reg) {
        if (reg.getStatus() == null || reg.getStatus().isEmpty()) {
            reg.setStatus("外出中");
        }
        return dao.insert(reg);
    }

    public boolean update(OutRegistration reg) {
        return dao.update(reg);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }
}
