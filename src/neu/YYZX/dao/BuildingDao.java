package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Building;

import java.util.List;

/**
 * 楼栋数据访问层
 */
public class BuildingDao extends BaseJsonDao<Building> {

    private static final String FILE_NAME = "buildings.json";
    private static final String ID_PREFIX = "B";
    private static final String ENTITY_TYPE = "building";

    public BuildingDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Building entity) {
        return entity.getBuildingId();
    }

    @Override
    protected void setEntityId(Building entity, String id) {
        entity.setBuildingId(id);
    }

    @Override
    protected TypeReference<List<Building>> getTypeReference() {
        return new TypeReference<List<Building>>() {};
    }
}
