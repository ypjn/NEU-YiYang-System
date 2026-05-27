package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api/elderly")
public class ElderlyController extends BaseController {

    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "") String keyword) {
        if (keyword.isEmpty()) {
            return success("ok", ctx.getElderlyDao().findAll());
        }
        List<Elderly> matched = ctx.getElderlyDao().findByName(keyword);
        Elderly byId = ctx.getElderlyDao().findById(keyword);
        if (byId != null && !matched.contains(byId)) {
            matched.add(0, byId);
        }
        return success("ok", matched);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable String id) {
        Elderly e = ctx.getElderlyDao().findById(id);
        if (e == null) return error("老人不存在");
        return success("ok", e);
    }

    @PostMapping("/checkin")
    public Map<String, Object> checkin(@RequestBody Map<String, String> body) {
        String bedId = body.getOrDefault("bedId", "");
        if (bedId.isEmpty()) bedId = null;
        if (bedId != null && ctx.getBedDao().findById(bedId) == null) {
            return error("床位不存在");
        }
        String level = body.get("nursingLevelCode");
        if (ctx.getNursingLevelDao().findByCode(level) == null) {
            return error("护理等级不存在");
        }

        Elderly e = new Elderly(null,
                body.get("name"),
                Integer.parseInt(body.getOrDefault("age", "0")),
                body.get("gender"),
                body.get("idCard"),
                body.get("phone"),
                body.get("address"),
                body.get("emergencyContact"),
                body.get("emergencyPhone"),
                body.get("checkinDate"),
                bedId,
                level,
                body.get("roomNo"),
                "在住");
        ctx.getElderlyDao().insert(e);

        if (bedId != null) {
            Bed bed = ctx.getBedDao().findById(bedId);
            if (bed != null) { bed.setStatus("占用"); ctx.getBedDao().update(bed); }
        }
        saveId();
        AuditLogger.log("入住登记", "老人管理", "老人 " + body.get("name") + " 办理入住");
        return success("入住登记成功", Map.of("id", e.getId()));
    }

    @PutMapping("/{id}/nursing-level")
    public Map<String, Object> setNursingLevel(@PathVariable String id,
                                               @RequestBody Map<String, String> body) {
        Elderly e = ctx.getElderlyDao().findById(id);
        if (e == null) return error("老人不存在");
        String newLevel = body.get("nursingLevelCode");
        if (ctx.getNursingLevelDao().findByCode(newLevel) == null) {
            return error("护理等级不存在");
        }
        e.setNursingLevelCode(newLevel);
        ctx.getElderlyDao().update(e);
        saveId();
        AuditLogger.log("设置护理等级", "老人管理", "老人 " + id + " 设置为 " + body.get("nursingLevelCode"));
        return success("护理等级设置成功");
    }
}
