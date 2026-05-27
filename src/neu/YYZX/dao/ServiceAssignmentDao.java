package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.ServiceAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * 管家服务分配数据访问层
 */
public class ServiceAssignmentDao extends BaseJsonDao<ServiceAssignment> {

    private static final String FILE_NAME = "service_assignments.json";
    private static final String ID_PREFIX = "SA";
    private static final String ENTITY_TYPE = "service_assignment";

    public ServiceAssignmentDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(ServiceAssignment entity) {
        return entity.getAssignmentId();
    }

    @Override
    protected void setEntityId(ServiceAssignment entity, String id) {
        entity.setAssignmentId(id);
    }

    @Override
    protected TypeReference<List<ServiceAssignment>> getTypeReference() {
        return new TypeReference<List<ServiceAssignment>>() {};
    }

    public List<ServiceAssignment> findByEmployeeId(String employeeId) {
        List<ServiceAssignment> result = new ArrayList<>();
        for (ServiceAssignment sa : list) {
            if (sa.getEmployeeId().equals(employeeId)) {
                result.add(sa);
            }
        }
        return result;
    }

    public List<ServiceAssignment> findByElderlyId(String elderlyId) {
        List<ServiceAssignment> result = new ArrayList<>();
        for (ServiceAssignment sa : list) {
            if (sa.getElderlyId().equals(elderlyId)) {
                result.add(sa);
            }
        }
        return result;
    }

    public List<ServiceAssignment> findByStatus(String status) {
        List<ServiceAssignment> result = new ArrayList<>();
        for (ServiceAssignment sa : list) {
            if (status.equals(sa.getStatus())) {
                result.add(sa);
            }
        }
        return result;
    }
}
