package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.CustomerCareProject;

import java.util.ArrayList;
import java.util.List;

public class CustomerCareProjectDao extends BaseJsonDao<CustomerCareProject> {

    private static final String FILE_NAME = "customer_care_projects.json";
    private static final String ID_PREFIX = "CCP";
    private static final String ENTITY_TYPE = "customer_care_project";

    public CustomerCareProjectDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(CustomerCareProject entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(CustomerCareProject entity, String id) {
        entity.setId(id);
    }

    @Override
    protected TypeReference<List<CustomerCareProject>> getTypeReference() {
        return new TypeReference<List<CustomerCareProject>>() {};
    }

    public List<CustomerCareProject> findByCustomerId(String customerId) {
        List<CustomerCareProject> result = new ArrayList<>();
        for (CustomerCareProject c : list) {
            if (c.getCustomerId() != null && c.getCustomerId().equals(customerId)) {
                result.add(c);
            }
        }
        return result;
    }

    public CustomerCareProject findByCustomerAndProject(String customerId, String projectCode) {
        for (CustomerCareProject c : list) {
            if (c.getCustomerId() != null && c.getCustomerId().equals(customerId)
                    && c.getProjectCode() != null && c.getProjectCode().equals(projectCode)) {
                return c;
            }
        }
        return null;
    }
}
