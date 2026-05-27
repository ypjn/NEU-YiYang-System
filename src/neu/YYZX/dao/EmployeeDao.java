package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工数据访问层
 */
public class EmployeeDao extends BaseJsonDao<Employee> {

    private static final String FILE_NAME = "employees.json";
    private static final String ID_PREFIX = "EMP";
    private static final String ENTITY_TYPE = "employee";

    public EmployeeDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Employee entity) {
        return entity.getEmployeeId();
    }

    @Override
    protected void setEntityId(Employee entity, String id) {
        entity.setEmployeeId(id);
    }

    @Override
    protected TypeReference<List<Employee>> getTypeReference() {
        return new TypeReference<List<Employee>>() {};
    }

    public List<Employee> findByName(String keyword) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : list) {
            if (e.getName() != null && e.getName().contains(keyword)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Employee> findByPosition(String position) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : list) {
            if (position.equals(e.getPosition())) {
                result.add(e);
            }
        }
        return result;
    }
}
