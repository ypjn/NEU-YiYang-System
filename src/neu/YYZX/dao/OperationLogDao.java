package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.OperationLog;

import java.util.List;

public class OperationLogDao extends BaseJsonDao<OperationLog> {

    private static final String FILE_NAME = "operation_logs.json";
    private static final String ID_PREFIX = "LOG";
    private static final String ENTITY_TYPE = "log";

    public OperationLogDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(OperationLog entity) {
        return entity.getLogId();
    }

    @Override
    protected void setEntityId(OperationLog entity, String id) {
        entity.setLogId(id);
    }

    @Override
    protected TypeReference<List<OperationLog>> getTypeReference() {
        return new TypeReference<List<OperationLog>>() {};
    }
}
