package neu.YYZX.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import neu.YYZX.common.AuditLogger;
import neu.YYZX.common.LoginContext;
import neu.YYZX.model.User;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class AuthInterceptor implements HandlerInterceptor {

    /** 管理员才能访问的路径前缀 */
    private static final Set<String> ADMIN_ONLY = Set.of(
            "/api/users",
            "/api/employees"
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String path = req.getRequestURI();
        String method = req.getMethod();

        // 放行 OPTIONS 预检
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // 放行登录和注册
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) return true;

        // 校验 token
        String token = req.getHeader("X-Auth-Token");
        if (token == null) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"message\":\"未登录或登录已过期\"}");
            return false;
        }
        User user = LoginContext.getUser(token);
        if (user == null) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"message\":\"未登录或登录已过期\"}");
            return false;
        }

        // 设置当前操作用户（供审计日志使用）
        AuditLogger.setCurrentUser(user);

        // 管理员专属路径校验
        if (ADMIN_ONLY.stream().anyMatch(path::startsWith) && !User.ROLE_ADMIN.equals(user.getRole())) {
            resp.setStatus(403);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"message\":\"权限不足，仅管理员可操作\"}");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        AuditLogger.clear();
    }
}
