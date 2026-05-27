package neu.YYZX.service;

import neu.YYZX.dao.CheckOutDao;
import neu.YYZX.model.CheckOut;

import java.util.List;

/**
 * 退住登记业务服务
 */
public class CheckOutService {
    private final CheckOutDao dao;

    public CheckOutService(CheckOutDao dao) {
        this.dao = dao;
    }

    public List<CheckOut> findAll() {
        return dao.findAll();
    }

    public CheckOut findById(String id) {
        return dao.findById(id);
    }

    public List<CheckOut> findByCustomerId(String customerId) {
        return dao.findByCustomerId(customerId);
    }

    public boolean add(CheckOut checkOut) {
        return dao.insert(checkOut);
    }

    public boolean update(CheckOut checkOut) {
        return dao.update(checkOut);
    }

    public boolean delete(String id) {
        return dao.delete(id);
    }
}
