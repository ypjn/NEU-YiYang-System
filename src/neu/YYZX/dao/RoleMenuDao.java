package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.RoleMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色菜单权限数据访问层
 */
public class RoleMenuDao extends BaseJsonDao<RoleMenu> {

    private static final String FILE_NAME = "role_menus.json";
    private static final String ID_PREFIX = "RM";
    private static final String ENTITY_TYPE = "role_menu";

    public RoleMenuDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(RoleMenu entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(RoleMenu entity, String id) {
        entity.setId(id);
    }

    @Override
    protected TypeReference<List<RoleMenu>> getTypeReference() {
        return new TypeReference<List<RoleMenu>>() {};
    }

    /** 查询指定角色的所有菜单权限 */
    public List<RoleMenu> findByRoleId(String roleId) {
        List<RoleMenu> result = new ArrayList<>();
        for (RoleMenu rm : list) {
            if (rm.getRoleId().equals(roleId)) {
                result.add(rm);
            }
        }
        return result;
    }

    /** 查询指定菜单对应的角色权限 */
    public List<RoleMenu> findByMenuId(String menuId) {
        List<RoleMenu> result = new ArrayList<>();
        for (RoleMenu rm : list) {
            if (rm.getMenuId().equals(menuId)) {
                result.add(rm);
            }
        }
        return result;
    }
}
