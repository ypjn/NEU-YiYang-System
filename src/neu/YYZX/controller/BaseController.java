package neu.YYZX.controller;

import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.model.User;
import neu.YYZX.service.DataInitializer;
import neu.YYZX.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class BaseController {

    protected final DataInitializer ctx = DataInitializer.getInstance();

    protected String nvl(String s) { return s == null ? "" : s; }

    protected void saveId() {
        PersistentIdGenerator.getInstance().save();
    }

    protected String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    protected Map<String, Object> success(String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("message", message);
        return m;
    }

    protected Map<String, Object> success(String message, Object data) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("message", message);
        m.put("data", data);
        return m;
    }

    protected Map<String, Object> error(String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", false);
        m.put("message", message);
        return m;
    }
}
