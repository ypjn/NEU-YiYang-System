package neu.YYZX.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import neu.YYZX.model.OperationLog;
import neu.YYZX.model.User;
import neu.YYZX.service.DataInitializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作审计日志工具
 */
public class AuditLogger {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void setCurrentUser(User user) {
        CURRENT_USER.set(user);
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    public static User getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static String getOperatorName() {
        User user = CURRENT_USER.get();
        if (user == null) return "未知";
        String name = user.getRealName();
        return (name != null && !name.isEmpty()) ? name : user.getUsername();
    }

    public static String getOperatorRole() {
        User user = CURRENT_USER.get();
        return user != null ? user.getRole() : "未知";
    }

    public static void log(String action, String target, String detail) {
        User user = CURRENT_USER.get();
        String name = user != null ? user.getRealName() : "未知";
        if (name == null || name.isEmpty()) name = user != null ? user.getUsername() : "未知";
        String role = user != null ? user.getRole() : "未知";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        OperationLog log = new OperationLog(null, name, role, action, target, detail, time);
        DataInitializer.getInstance().getOperationLogDao().insert(log);

        // 触发 ID 持久化
        PersistentIdGenerator.getInstance().save();
    }

    /**
     * 记录一条可撤销的操作日志
     * @param action 操作动作
     * @param target 操作目标模块
     * @param detail 详细信息
     * @param reversibleData 撤销所需的JSON数据（可null）
     */
    public static void logReversible(String action, String target, String detail, Object reversibleData) {
        User user = CURRENT_USER.get();
        String name = user != null ? user.getRealName() : "未知";
        if (name == null || name.isEmpty()) name = user != null ? user.getUsername() : "未知";
        String role = user != null ? user.getRole() : "未知";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        OperationLog log = new OperationLog(null, name, role, action, target, detail, time);
        if (reversibleData != null) {
            try {
                log.setReversibleData(MAPPER.writeValueAsString(reversibleData));
            } catch (JsonProcessingException e) {
                System.out.println("序列化撤销数据失败: " + e.getMessage());
            }
        }
        DataInitializer.getInstance().getOperationLogDao().insert(log);
        PersistentIdGenerator.getInstance().save();
    }

    /**
     * 将撤销数据JSON反序列化为Map
     */
    @SuppressWarnings("unchecked")
    public static java.util.Map<String, Object> parseReversibleData(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return MAPPER.readValue(json, java.util.Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
