package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Role;

import java.util.List;

/**
 * 角色数据访问层
 */
public class RoleDao extends BaseJsonDao<Role> {

    private static final String FILE_NAME = "roles.json";
    private static final String ID_PREFIX = "RL";
    private static final String ENTITY_TYPE = "role";

    public RoleDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Role entity) {
        return entity.getRoleId();
    }

    @Override
    protected void setEntityId(Role entity, String id) {
        entity.setRoleId(id);
    }

    @Override
    protected TypeReference<List<Role>> getTypeReference() {
        return new TypeReference<List<Role>>() {};
    }
}
