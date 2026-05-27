package neu.YYZX.controller;

import neu.YYZX.model.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController extends BaseController {

    @GetMapping
    public Map<String, Object> list(@RequestParam(defaultValue = "") String keyword) {
        List<OperationLog> all = ctx.getOperationLogDao().findAll();
        if (keyword.isEmpty()) {
            // 倒序返回，最新的在前
            List<OperationLog> reversed = new ArrayList<>(all);
            Collections.reverse(reversed);
            return success("ok", reversed);
        }
        List<OperationLog> result = new ArrayList<>();
        for (OperationLog log : all) {
            if (log.getOperatorName().contains(keyword)
                    || log.getAction().contains(keyword)
                    || log.getTarget().contains(keyword)) {
                result.add(log);
            }
        }
        Collections.reverse(result);
        return success("ok", result);
    }
}
