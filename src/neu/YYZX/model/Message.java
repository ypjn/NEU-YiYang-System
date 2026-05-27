package neu.YYZX.model;

/**
 * 消息通知实体
 */
public class Message {
    private String messageId;
    private String receiverName;
    private String content;
    private String time;
    private boolean isRead;

    public Message() {}

    public Message(String messageId, String receiverName, String content, String time, boolean isRead) {
        this.messageId = messageId;
        this.receiverName = receiverName;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
