package neu.YYZX.common;

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
}
