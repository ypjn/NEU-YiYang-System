package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api/health-records")
public class HealthRecordController extends BaseController {

    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "") String elderlyId) {
        if (!elderlyId.isEmpty()) {
            return success("ok", ctx.getHealthRecordDao().findByCustomerId(elderlyId));
        }
        return success("ok", ctx.getHealthRecordDao().findAll());
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody HealthRecord record) {
        if (ctx.getElderlyDao().findById(record.getCustomerId()) == null) {
            return error("老人不存在");
        }
        ctx.getHealthRecordDao().insert(record);
        saveId();
        AuditLogger.log("登记健康记录", "健康管理", "老人 " + record.getCustomerId() + " 健康记录");
        return success("健康记录登记成功");
    }
}
