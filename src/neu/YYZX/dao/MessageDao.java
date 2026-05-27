package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.Message;

import java.util.List;

public class MessageDao extends BaseJsonDao<Message> {

    private static final String FILE_NAME = "messages.json";
    private static final String ID_PREFIX = "MSG";
    private static final String ENTITY_TYPE = "message";

    public MessageDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(Message entity) { return entity.getMessageId(); }

    @Override
    protected void setEntityId(Message entity, String id) { entity.setMessageId(id); }

    @Override
    protected TypeReference<List<Message>> getTypeReference() {
        return new TypeReference<List<Message>>() {};
    }

    public List<Message> findByReceiver(String receiverName) {
        List<Message> result = new java.util.ArrayList<>();
        for (Message m : list) {
            if (m.getReceiverName() != null && m.getReceiverName().equals(receiverName)) {
                result.add(m);
            }
        }
        return result;
    }
}
