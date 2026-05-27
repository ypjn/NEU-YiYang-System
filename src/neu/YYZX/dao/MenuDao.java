package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.SysMenu;

import java.util.List;

/**
 * 系统菜单数据访问层
 */
public class MenuDao extends BaseJsonDao<SysMenu> {

    private static final String FILE_NAME = "menus.json";
    private static final String ID_PREFIX = "M";
    private static final String ENTITY_TYPE = "menu";

    public MenuDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(SysMenu entity) {
        return entity.getMenuId();
    }

    @Override
    protected void setEntityId(SysMenu entity, String id) {
        entity.setMenuId(id);
    }

    @Override
    protected TypeReference<List<SysMenu>> getTypeReference() {
        return new TypeReference<List<SysMenu>>() {};
    }
}
