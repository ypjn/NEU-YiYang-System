package neu.YYZX.service;

import neu.YYZX.dao.CustomerCareProjectDao;
import neu.YYZX.model.CustomerCareProject;

import java.util.List;

public class CustomerCareProjectService {

    private final CustomerCareProjectDao dao;

    public CustomerCareProjectService(CustomerCareProjectDao dao) {
        this.dao = dao;
    }

    public void add(CustomerCareProject c) { dao.insert(c); }
    public void update(CustomerCareProject c) { dao.update(c); }
    public void delete(String id) { dao.delete(id); }
    public List<CustomerCareProject> findByCustomerId(String customerId) {
        return dao.findByCustomerId(customerId);
    }
    public CustomerCareProject findByCustomerAndProject(String customerId, String projectCode) {
        return dao.findByCustomerAndProject(customerId, projectCode);
    }
    public List<CustomerCareProject> findAll() { return dao.findAll(); }
}
