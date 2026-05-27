package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api/nursing")
public class NursingController extends BaseController {

    // ===== 护理级别 =====

    @GetMapping("/levels")
    public Map<String, Object> listLevels() {
        return success("ok", ctx.getNursingLevelDao().findAll());
    }

    @PostMapping("/levels")
    public Map<String, Object> addLevel(@RequestBody NursingLevel level) {
        if (ctx.getNursingLevelDao().findByCode(level.getCode()) != null) {
            return error("编码已存在");
        }
        ctx.getNursingLevelDao().insert(level);
        saveId();
        AuditLogger.log("添加护理级别", "护理管理", "添加级别 " + level.getCode());
        return success("添加成功");
    }

    // ===== 护理项目 =====

    @GetMapping("/projects")
    public Map<String, Object> listProjects() {
        return success("ok", ctx.getCareProjectDao().findAll());
    }

    @GetMapping("/projects/applicable/{levelCode}")
    public Map<String, Object> applicableProjects(@PathVariable String levelCode) {
        List<CareProject> all = ctx.getCareProjectDao().findAll();
        List<CareProject> result = new ArrayList<>();
        for (CareProject p : all) {
            if (p.getRemark() != null && (p.getRemark().contains("所有护理级") ||
                    p.getRemark().contains(levelCode))) {
                result.add(p);
            }
        }
        return success("ok", result);
    }

    @PostMapping("/projects")
    public Map<String, Object> addProject(@RequestBody CareProject project) {
        if (ctx.getCareProjectDao().findByCode(project.getCode()) != null) {
            return error("编码已存在");
        }
        ctx.getCareProjectDao().insert(project);
        saveId();
        AuditLogger.log("添加护理项目", "护理管理", "添加项目 " + project.getCode());
        return success("添加成功");
    }

    // ===== 护理记录 =====

    @GetMapping("/records")
    public Map<String, Object> listRecords() {
        return success("ok", ctx.getCareRecordDao().findAll());
    }

    @GetMapping("/records/nurse/{nurseName}")
    public Map<String, Object> listNurseRecords(@PathVariable String nurseName) {
        return success("ok", ctx.getCareRecordDao().findByNurseName(nurseName));
    }

    @PostMapping("/records")
    public Map<String, Object> createRecord(@RequestBody Map<String, String> body) {
        if (ctx.getElderlyDao().findById(body.get("elderlyId")) == null) {
            return error("老人不存在");
        }
        CareRecord record = new CareRecord(null,
                body.get("elderlyId"),
                body.get("projectCode"),
                now(),
                body.get("nurseName"),
                body.getOrDefault("remark", ""));
        ctx.getCareRecordDao().insert(record);
        saveId();
        AuditLogger.log("登记护理记录", "护理管理", "老人 " + body.get("elderlyId") + " 执行 " + body.get("projectCode"));
        return success("护理执行登记成功", Map.of("id", record.getId()));
    }
}
