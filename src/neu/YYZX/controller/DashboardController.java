package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class DashboardController extends BaseController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> stats = new LinkedHashMap<>();

        List<Elderly> elders = ctx.getElderlyDao().findAll();
        long elderlyTotal = elders.size();
        long elderlyActive = elders.stream()
                .filter(e -> !"退住".equals(e.getStatus())).count();

        List<Bed> beds = ctx.getBedDao().findAll();
        long bedTotal = beds.size();
        long bedOccupied = beds.stream()
                .filter(b -> "占用".equals(b.getStatus())).count();

        long outCount = ctx.getOutRegistrationDao().findByStatus("外出中").size();
        long checkoutPending = ctx.getCheckOutDao().findAll().size();
        long employeeCount = ctx.getEmployeeDao().findAll().size();
        long todayRecords = ctx.getCareRecordDao().findAll().size();

        stats.put("elderlyTotal", elderlyTotal);
        stats.put("elderlyActive", elderlyActive);
        stats.put("bedTotal", bedTotal);
        stats.put("bedOccupied", bedOccupied);
        stats.put("bedFree", bedTotal - bedOccupied);
        stats.put("outCount", outCount);
        stats.put("checkoutPending", checkoutPending);
        stats.put("employeeCount", employeeCount);
        stats.put("todayRecords", todayRecords);

        return success("ok", stats);
    }
}
