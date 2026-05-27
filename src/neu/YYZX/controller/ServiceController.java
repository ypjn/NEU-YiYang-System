package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api/services")
public class ServiceController extends BaseController {

    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "") String status) {
        List<ServiceAssignment> list;
        if (!status.isEmpty()) {
            list = ctx.getServiceAssignmentDao().findByStatus(status);
        } else {
            list = ctx.getServiceAssignmentDao().findAll();
        }
        return success("ok", list);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> body) {
        String empId = body.get("employeeId");
        if (ctx.getEmployeeDao().findById(empId) == null) return error("员工不存在");
        String eid = body.get("elderlyId");
        if (ctx.getElderlyDao().findById(eid) == null) return error("老人不存在");

        String operatorName = AuditLogger.getOperatorName();

        ctx.getServiceAssignmentDao().insert(new ServiceAssignment(null,
                empId, eid,
                body.get("serviceType"),
                body.get("startDate"),
                body.get("endDate"),
                Double.parseDouble(body.getOrDefault("fee", "0")),
                "服务中",
                body.getOrDefault("remark", ""),
                operatorName));
        saveId();
        AuditLogger.log("服务购买", "管家服务", "老人 " + body.get("elderlyId") + " 购买 " + body.get("serviceType"));
        return success("服务购买成功");
    }

    @PutMapping("/{id}/followup")
    public Map<String, Object> followup(@PathVariable String id,
                                        @RequestBody Map<String, String> body) {
        ServiceAssignment sa = ctx.getServiceAssignmentDao().findById(id);
        if (sa == null) return error("记录不存在");
        String note = body.get("note");
        String oldRemark = nvl(sa.getRemark());
        sa.setRemark(oldRemark + (oldRemark.isEmpty() ? "" : "; ") + "[关注]" + note);
        ctx.getServiceAssignmentDao().update(sa);
        saveId();
        AuditLogger.log("服务关注", "管家服务", "服务 " + id + " 添加关注备注");
        return success("关注备注已添加");
    }

    @PutMapping("/{id}/renew")
    public Map<String, Object> renew(@PathVariable String id,
                                     @RequestBody Map<String, String> body) {
        ServiceAssignment sa = ctx.getServiceAssignmentDao().findById(id);
        if (sa == null) return error("记录不存在");
        double fee = Double.parseDouble(body.getOrDefault("fee", "0"));
        sa.setEndDate(body.get("endDate"));
        sa.setFee(sa.getFee() + fee);
        sa.setStatus("已续费");
        ctx.getServiceAssignmentDao().update(sa);
        saveId();
        AuditLogger.log("服务续费", "管家服务", "服务 " + id + " 续费");
        return success("续费成功");
    }

    @PutMapping("/{id}/revoke")
    public Map<String, Object> revoke(@PathVariable String id) {
        ServiceAssignment sa = ctx.getServiceAssignmentDao().findById(id);
        if (sa == null) return error("服务记录不存在");

        if ("已到期".equals(sa.getStatus())) return error("该服务已到期，无法撤销");
        if ("已撤销".equals(sa.getStatus())) return error("该服务已被撤销");

        String currentName = AuditLogger.getOperatorName();
        String currentRole = AuditLogger.getOperatorRole();

        // 护工只能撤销自己的服务
        if (!"admin".equals(currentRole)) {
            if (!currentName.equals(sa.getOperatorName())) {
                return error("只能撤销自己办理的服务");
            }
        }

        sa.setStatus("已撤销");
        ctx.getServiceAssignmentDao().update(sa);
        saveId();
        AuditLogger.log("撤销服务", "管家服务", "服务 " + id + " 已撤销");

        // 管理员撤销时，通知对应护工
        if ("admin".equals(currentRole) && sa.getOperatorName() != null) {
            ctx.getMessageDao().insert(new Message(null,
                    sa.getOperatorName(),
                    "管理员 " + currentName + " 撤销了您为老人 " + sa.getElderlyId() + " 办理的 " + sa.getServiceType() + " 服务",
                    now(),
                    false));
            saveId();
        }

        return success("服务已撤销");
    }
}
