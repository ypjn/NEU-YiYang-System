package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api")
public class OutCheckoutController extends BaseController {

    // ===== 外出登记 =====

    @GetMapping("/out-registrations")
    public Map<String, Object> listOut(@RequestParam(defaultValue = "") String status,
                                       @RequestParam(defaultValue = "") String customerId) {
        List<OutRegistration> list;
        if (!customerId.isEmpty()) {
            list = ctx.getOutRegistrationDao().findByCustomerId(customerId);
        } else if (!status.isEmpty()) {
            list = ctx.getOutRegistrationDao().findByStatus(status);
        } else {
            list = ctx.getOutRegistrationDao().findAll();
        }
        return success("ok", list);
    }

    @PostMapping("/out-registrations")
    public Map<String, Object> addOut(@RequestBody Map<String, String> body) {
        if (ctx.getElderlyDao().findById(body.get("customerId")) == null) {
            return error("老人不存在");
        }
        ctx.getOutRegistrationDao().insert(new OutRegistration(null,
                body.get("customerId"),
                body.get("outTime"),
                body.get("expectedReturnTime"),
                null,
                body.getOrDefault("companion", ""),
                body.getOrDefault("reason", ""),
                "外出中"));
        saveId();
        AuditLogger.log("外出登记", "外出管理", "老人 " + body.get("customerId") + " 外出登记");
        return success("外出登记成功");
    }

    @PutMapping("/out-registrations/{id}/return")
    public Map<String, Object> markReturn(@PathVariable String id,
                                          @RequestBody Map<String, String> body) {
        OutRegistration o = ctx.getOutRegistrationDao().findById(id);
        if (o == null) return error("记录不存在");
        o.setActualReturnTime(body.getOrDefault("actualReturnTime", now()));
        o.setStatus("已归来");
        ctx.getOutRegistrationDao().update(o);
        saveId();
        AuditLogger.log("登记归来", "外出管理", "外出记录 " + id + " 已归来");
        return success("已登记归来");
    }

    @PutMapping("/out-registrations/{id}/timeout")
    public Map<String, Object> markTimeout(@PathVariable String id) {
        OutRegistration o = ctx.getOutRegistrationDao().findById(id);
        if (o == null) return error("记录不存在");
        o.setStatus("超时未归");
        ctx.getOutRegistrationDao().update(o);
        saveId();
        AuditLogger.log("标记超时", "外出管理", "外出记录 " + id + " 超时未归");
        return success("已标记超时");
    }

    // ===== 退住 =====

    @PostMapping("/checkouts")
    public Map<String, Object> createCheckout(@RequestBody Map<String, String> body) {
        String customerId = body.get("customerId");
        if (ctx.getElderlyDao().findById(customerId) == null) {
            return error("老人不存在");
        }
        ctx.getCheckOutDao().insert(new CheckOut(null,
                customerId,
                body.getOrDefault("checkoutDate", now().substring(0, 10)),
                body.getOrDefault("reason", ""),
                body.getOrDefault("remark", "")));
        saveId();
        AuditLogger.log("退住登记", "退住管理", "老人 " + body.get("customerId") + " 办理退住");
        return success("退住登记成功");
    }

    @GetMapping("/checkouts")
    public Map<String, Object> listCheckouts(@RequestParam(defaultValue = "") String customerId) {
        List<CheckOut> list;
        if (!customerId.isEmpty()) {
            list = ctx.getCheckOutDao().findByCustomerId(customerId);
        } else {
            list = ctx.getCheckOutDao().findAll();
        }
        return success("ok", list);
    }

    @PutMapping("/checkouts/{id}/confirm")
    public Map<String, Object> confirmCheckout(@PathVariable String id) {
        CheckOut c = ctx.getCheckOutDao().findById(id);
        if (c == null) return error("记录不存在");
        Elderly e = ctx.getElderlyDao().findById(c.getCustomerId());
        if (e != null) {
            if (e.getBedId() != null) {
                Bed bed = ctx.getBedDao().findById(e.getBedId());
                if (bed != null) { bed.setStatus("空闲"); ctx.getBedDao().update(bed); }
            }
            e.setStatus("退住");
            ctx.getElderlyDao().update(e);
        }
        saveId();
        AuditLogger.log("确认退住", "退住管理", "退住记录 " + id + " 已确认");
        return success("退住确认成功，床位已释放");
    }

    @DeleteMapping("/checkouts/{id}")
    public Map<String, Object> revokeCheckout(@PathVariable String id) {
        if (!ctx.getCheckOutDao().delete(id)) return error("记录不存在");
        saveId();
        AuditLogger.log("撤销退住", "退住管理", "退住记录 " + id + " 已撤销");
        return success("退住记录已撤销");
    }
}
