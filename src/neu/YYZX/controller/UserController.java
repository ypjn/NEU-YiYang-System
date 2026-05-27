package neu.YYZX.controller;

import neu.YYZX.common.AuditLogger;
import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {

    @GetMapping
    public Map<String, Object> listAll() {
        List<User> users = ctx.getUserDao().findAll();
        return success("ok", users);
    }

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam(defaultValue = "") String keyword,
                                      @RequestParam(defaultValue = "") String type) {
        if (type.equals("username")) {
            User u = ctx.getUserDao().findByUsername(keyword);
            return success("ok", u != null ? List.of(u) : List.of());
        }
        if (type.equals("role")) {
            return success("ok", ctx.getUserDao().findByRole(keyword));
        }
        // name search
        List<User> all = ctx.getUserDao().findAll();
        List<User> result = new ArrayList<>();
        for (User u : all) {
            if (u.getRealName() != null && u.getRealName().contains(keyword)) {
                result.add(u);
            }
        }
        return success("ok", result);
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (ctx.getUserDao().findByUsername(username) != null) {
            return error("账号已存在");
        }
        String role = body.get("role");
        if (!User.ROLE_ADMIN.equals(role) && !User.ROLE_NURSE.equals(role)) {
            return error("角色无效");
        }
        ctx.getUserDao().insert(new User(null, username, body.get("password"),
                role, body.get("realName"), body.get("phone"), now()));
        saveId();
        AuditLogger.log("新增用户", "用户管理", "添加用户 " + username + "（" + role + "）");
        return success("添加成功");
    }

    @PutMapping("/{username}")
    public Map<String, Object> update(@PathVariable String username,
                                      @RequestBody Map<String, String> body) {
        User u = ctx.getUserDao().findByUsername(username);
        if (u == null) return error("用户不存在");
        if (body.containsKey("password") && !body.get("password").isEmpty())
            u.setPassword(body.get("password"));
        if (body.containsKey("realName") && !body.get("realName").isEmpty())
            u.setRealName(body.get("realName"));
        if (body.containsKey("phone") && !body.get("phone").isEmpty())
            u.setPhone(body.get("phone"));
        if (body.containsKey("role") && !body.get("role").isEmpty())
            u.setRole(body.get("role"));
        ctx.getUserDao().update(u);
        saveId();
        AuditLogger.log("修改用户", "用户管理", "修改用户 " + username);
        return success("修改成功");
    }

    @DeleteMapping("/{username}")
    public Map<String, Object> delete(@PathVariable String username) {
        User u = ctx.getUserDao().findByUsername(username);
        if (u == null) return error("用户不存在");
        if (User.ROLE_ADMIN.equals(u.getRole()) && ctx.getUserDao().findByRole(User.ROLE_ADMIN).size() <= 1) {
            return error("不能删除最后一个管理员");
        }
        ctx.getUserDao().delete(u.getUserId());
        saveId();
        AuditLogger.log("删除用户", "用户管理", "删除用户 " + username);
        return success("删除成功");
    }
}
