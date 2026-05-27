package neu.YYZX.controller;

import neu.YYZX.common.LoginContext;
import neu.YYZX.model.User;
import neu.YYZX.service.DataInitializer;
import neu.YYZX.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController() {
        this.userService = new UserService(DataInitializer.getInstance().getUserDao());
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String realName = body.getOrDefault("realName", "");
        String phone = body.getOrDefault("phone", "");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Map.of("success", false, "message", "账号和密码不能为空");
        }
        if (userService.containsUsername(username)) {
            return Map.of("success", false, "message", "账号已存在");
        }
        userService.addUser(new User(null, username, password, "nurse", realName, phone,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        return Map.of("success", true, "message", "注册成功，请登录");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");

        User user = userService.authenticate(username, password, role);
        if (user == null) {
            return Map.of("success", false, "message", "账号、密码或角色不正确");
        }
        String token = LoginContext.login(user);
        return Map.of(
            "success", true,
            "token", token,
            "userId", user.getUserId(),
            "username", user.getUsername(),
            "realName", user.getRealName() != null ? user.getRealName() : "",
            "role", user.getRole()
        );
    }
}
