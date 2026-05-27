package neu.YYZX.service;

import neu.YYZX.dao.BedDetailDao;
import neu.YYZX.model.BedDetail;

import java.util.List;

/**
 * 床位详情业务服务
 */
public class BedDetailService {
    private final BedDetailDao dao;
    public BedDetailService(BedDetailDao dao) { this.dao = dao; }
    public List<BedDetail> findAll() { return dao.findAll(); }
    public BedDetail findById(String id) { return dao.findById(id); }
    public List<BedDetail> findByBedId(String bedId) { return dao.findByBedId(bedId); }
    public List<BedDetail> findByCustomerId(String cid) { return dao.findByCustomerId(cid); }
    public boolean add(BedDetail bd) { return dao.insert(bd); }
    public boolean update(BedDetail bd) { return dao.update(bd); }
    public boolean delete(String id) { return dao.delete(id); }
}
