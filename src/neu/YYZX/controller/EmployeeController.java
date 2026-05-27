package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController extends BaseController {

    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "") String position,
                                    @RequestParam(defaultValue = "") String name) {
        List<Employee> list;
        if (!name.isEmpty()) {
            list = ctx.getEmployeeDao().findByName(name);
        } else if (!position.isEmpty()) {
            list = ctx.getEmployeeDao().findByPosition(position);
        } else {
            list = ctx.getEmployeeDao().findAll();
        }
        return success("ok", list);
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody Employee emp) {
        ctx.getEmployeeDao().insert(emp);
        saveId();
        AuditLogger.log("添加员工", "员工管理", "添加员工 " + emp.getName());
        return success("添加成功");
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable String id, @RequestBody Map<String, String> body) {
        Employee e = ctx.getEmployeeDao().findById(id);
        if (e == null) return error("员工不存在");
        if (body.containsKey("name")) e.setName(body.get("name"));
        if (body.containsKey("gender")) e.setGender(body.get("gender"));
        if (body.containsKey("position")) e.setPosition(body.get("position"));
        if (body.containsKey("phone")) e.setPhone(body.get("phone"));
        if (body.containsKey("salary")) e.setSalary(Double.parseDouble(body.get("salary")));
        if (body.containsKey("status")) e.setStatus(body.get("status"));
        ctx.getEmployeeDao().update(e);
        saveId();
        AuditLogger.log("修改员工", "员工管理", "修改员工 " + id);
        return success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable String id) {
        if (ctx.getEmployeeDao().delete(id)) {
            saveId();
            AuditLogger.log("删除员工", "员工管理", "删除员工 " + id);
            return success("删除成功");
        }
        return error("员工不存在");
    }
}
