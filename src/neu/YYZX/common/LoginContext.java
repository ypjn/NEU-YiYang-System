package neu.YYZX.common;

import neu.YYZX.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录上下文 —— token ↔ User 映射
 */
public class LoginContext {
    private static final Map<String, User> TOKEN_MAP = new ConcurrentHashMap<>();

    public static String login(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        TOKEN_MAP.put(token, user);
        return token;
    }

    public static User getUser(String token) {
        return TOKEN_MAP.get(token);
    }

    public static void logout(String token) {
        TOKEN_MAP.remove(token);
    }

    public static boolean isAdmin(String token) {
        User u = TOKEN_MAP.get(token);
        return u != null && User.ROLE_ADMIN.equals(u.getRole());
    }

    public static boolean isNurse(String token) {
        User u = TOKEN_MAP.get(token);
        return u != null && User.ROLE_NURSE.equals(u.getRole());
    }
}
