package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api/diet")
public class DietController extends BaseController {

    // ===== 食材管理 =====

    @GetMapping("/foods")
    public Map<String, Object> listFoods() {
        return success("ok", ctx.getFoodDao().findAll());
    }

    @PostMapping("/foods")
    public Map<String, Object> addFood(@RequestBody Map<String, String> body) {
        ctx.getFoodDao().insert(new Food(null,
                body.get("foodName"),
                body.getOrDefault("category", ""),
                body.getOrDefault("unit", ""),
                Double.parseDouble(body.getOrDefault("price", "0")),
                body.getOrDefault("nutrition", ""),
                body.getOrDefault("remark", "")));
        saveId();
        AuditLogger.log("添加食材", "膳食管理", "添加食材 " + body.get("foodName"));
        return success("添加食材成功");
    }

    @PutMapping("/foods/{id}")
    public Map<String, Object> updateFood(@PathVariable String id, @RequestBody Map<String, String> body) {
        Food f = ctx.getFoodDao().findById(id);
        if (f == null) return error("食材不存在");
        if (body.containsKey("foodName")) f.setFoodName(body.get("foodName"));
        if (body.containsKey("category")) f.setCategory(body.get("category"));
        if (body.containsKey("unit")) f.setUnit(body.get("unit"));
        if (body.containsKey("price")) f.setPrice(Double.parseDouble(body.get("price")));
        if (body.containsKey("nutrition")) f.setNutrition(body.get("nutrition"));
        if (body.containsKey("remark")) f.setRemark(body.get("remark"));
        ctx.getFoodDao().update(f);
        saveId();
        AuditLogger.log("修改食材", "膳食管理", "修改食材 " + id);
        return success("更新成功");
    }

    @DeleteMapping("/foods/{id}")
    public Map<String, Object> deleteFood(@PathVariable String id) {
        if (!ctx.getFoodDao().delete(id)) return error("食材不存在");
        saveId();
        AuditLogger.log("删除食材", "膳食管理", "删除食材 " + id);
        return success("删除成功");
    }

    // ===== 膳食日历 =====

    @GetMapping("/calendar")
    public Map<String, Object> listCalendar() {
        return success("ok", ctx.getDietCalendarDao().findAll());
    }

    @PostMapping("/calendar")
    public Map<String, Object> addCalendar(@RequestBody Map<String, String> body) {
        ctx.getDietCalendarDao().insert(new DietCalendar(null,
                body.get("calendarDate"),
                body.getOrDefault("mealType", "午餐"),
                body.getOrDefault("foodIds", ""),
                body.getOrDefault("remark", "")));
        saveId();
        AuditLogger.log("添加食谱", "膳食管理", "日期 " + body.get("calendarDate") + " " + body.get("mealType"));
        return success("添加食谱成功");
    }

    @PutMapping("/calendar/{id}")
    public Map<String, Object> updateCalendar(@PathVariable String id, @RequestBody Map<String, String> body) {
        DietCalendar dc = ctx.getDietCalendarDao().findById(id);
        if (dc == null) return error("记录不存在");
        if (body.containsKey("calendarDate")) dc.setCalendarDate(body.get("calendarDate"));
        if (body.containsKey("mealType")) dc.setMealType(body.get("mealType"));
        if (body.containsKey("foodIds")) dc.setFoodIds(body.get("foodIds"));
        if (body.containsKey("remark")) dc.setRemark(body.get("remark"));
        ctx.getDietCalendarDao().update(dc);
        saveId();
        AuditLogger.log("修改食谱", "膳食管理", "修改食谱 " + id);
        return success("更新成功");
    }

    @DeleteMapping("/calendar/{id}")
    public Map<String, Object> deleteCalendar(@PathVariable String id) {
        if (!ctx.getDietCalendarDao().delete(id)) return error("记录不存在");
        saveId();
        AuditLogger.log("删除食谱", "膳食管理", "删除食谱 " + id);
        return success("删除成功");
    }

    // ===== 饮食偏好 =====

    @GetMapping("/preferences")
    public Map<String, Object> listPreferences(@RequestParam(defaultValue = "") String elderlyId) {
        if (!elderlyId.isEmpty()) {
            return success("ok", ctx.getDietPreferenceDao().findByCustomerId(elderlyId));
        }
        return success("ok", ctx.getDietPreferenceDao().findAll());
    }

    @PostMapping("/preferences")
    public Map<String, Object> addPreference(@RequestBody Map<String, String> body) {
        ctx.getDietPreferenceDao().insert(new DietPreference(null,
                body.get("customerId"),
                body.getOrDefault("preferenceType", ""),
                body.getOrDefault("description", ""),
                body.getOrDefault("allergies", ""),
                body.getOrDefault("taboos", ""),
                body.getOrDefault("remark", "")));
        saveId();
        AuditLogger.log("添加饮食偏好", "膳食管理", "老人 " + body.get("customerId") + " 偏好记录");
        return success("添加饮食偏好成功");
    }

    @PutMapping("/preferences/{id}")
    public Map<String, Object> updatePreference(@PathVariable String id, @RequestBody Map<String, String> body) {
        DietPreference dp = ctx.getDietPreferenceDao().findById(id);
        if (dp == null) return error("记录不存在");
        if (body.containsKey("preferenceType")) dp.setPreferenceType(body.get("preferenceType"));
        if (body.containsKey("description")) dp.setDescription(body.get("description"));
        if (body.containsKey("allergies")) dp.setAllergies(body.get("allergies"));
        if (body.containsKey("taboos")) dp.setTaboos(body.get("taboos"));
        if (body.containsKey("remark")) dp.setRemark(body.get("remark"));
        ctx.getDietPreferenceDao().update(dp);
        saveId();
        AuditLogger.log("修改饮食偏好", "膳食管理", "修改偏好 " + id);
        return success("更新成功");
    }

    @DeleteMapping("/preferences/{id}")
    public Map<String, Object> deletePreference(@PathVariable String id) {
        if (!ctx.getDietPreferenceDao().delete(id)) return error("记录不存在");
        saveId();
        AuditLogger.log("删除饮食偏好", "膳食管理", "删除偏好 " + id);
        return success("删除成功");
    }
}
