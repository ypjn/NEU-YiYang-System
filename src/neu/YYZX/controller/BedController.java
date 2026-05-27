package neu.YYZX.controller;

import neu.YYZX.model.*;
import org.springframework.web.bind.annotation.*;
import neu.YYZX.common.AuditLogger;

import java.util.*;

@RestController
@RequestMapping("/api")
public class BedController extends BaseController {

    // ===== 楼栋 =====

    @GetMapping("/buildings")
    public Map<String, Object> listBuildings() {
        return success("ok", ctx.getBuildingDao().findAll());
    }

    // ===== 房间 =====

    @GetMapping("/rooms")
    public Map<String, Object> listRooms() {
        return success("ok", ctx.getRoomDao().findAll());
    }

    @PostMapping("/rooms")
    public Map<String, Object> addRoom(@RequestBody Map<String, String> body) {
        Room room = new Room(null,
                body.get("roomNo"),
                body.get("buildingId"),
                Integer.parseInt(body.getOrDefault("floor", "1")),
                body.get("roomType"),
                Integer.parseInt(body.getOrDefault("capacity", "2")),
                Double.parseDouble(body.getOrDefault("price", "3000")),
                body.getOrDefault("status", "空闲"));
        ctx.getRoomDao().insert(room);
        saveId();
        AuditLogger.log("添加房间", "床位管理", "添加房间 " + body.get("roomNo"));
        return success("房间添加成功", Map.of("roomId", room.getRoomId()));
    }

    // ===== 床位示意图 =====

    @GetMapping("/beds/diagram")
    public Map<String, Object> bedDiagram() {
        List<Room> rooms = ctx.getRoomDao().findAll();
        List<Bed> beds = ctx.getBedDao().findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Room room : rooms) {
            Map<String, Object> roomData = new LinkedHashMap<>();
            Building building = ctx.getBuildingDao().findById(room.getBuildingId());
            roomData.put("buildingName", building != null ? building.getBuildingName() : "未知楼栋");
            roomData.put("floor", room.getFloor());
            roomData.put("roomNo", room.getRoomNo());
            roomData.put("roomType", room.getRoomType());
            roomData.put("price", room.getPrice());

            List<Map<String, Object>> roomBeds = new ArrayList<>();
            for (Bed b : beds) {
                if (b.getRoomId().equals(room.getRoomId())) {
                    Map<String, Object> bd = new LinkedHashMap<>();
                    bd.put("bedId", b.getBedId());
                    bd.put("bedNo", b.getBedNo());
                    bd.put("status", b.getStatus());
                    Elderly occupant = ctx.getElderlyDao().findByBedId(b.getBedId());
                    bd.put("occupantName", occupant != null ? occupant.getName() : "");
                    roomBeds.add(bd);
                }
            }
            roomData.put("beds", roomBeds);
            result.add(roomData);
        }
        return success("ok", result);
    }

    @GetMapping("/beds/available")
    public Map<String, Object> availableBeds() {
        List<Bed> all = ctx.getBedDao().findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Bed b : all) {
            if ("空闲".equals(b.getStatus())) {
                Room room = ctx.getRoomDao().findById(b.getRoomId());
                Map<String, Object> bd = new LinkedHashMap<>();
                bd.put("bedId", b.getBedId());
                bd.put("bedNo", b.getBedNo());
                bd.put("roomNo", room != null ? room.getRoomNo() : "");
                bd.put("roomType", room != null ? room.getRoomType() : "");
                bd.put("price", room != null ? room.getPrice() : 0);
                result.add(bd);
            }
        }
        return success("ok", result);
    }

    @PostMapping("/beds")
    public Map<String, Object> addBed(@RequestBody Map<String, String> body) {
        String roomId = body.get("roomId");
        if (ctx.getRoomDao().findById(roomId) == null) return error("房间不存在");
        ctx.getBedDao().insert(new Bed(null, body.get("bedNo"), roomId, "空闲"));
        saveId();
        AuditLogger.log("添加床位", "床位管理", "添加床位 " + body.get("bedNo"));
        return success("添加成功");
    }

    @PutMapping("/beds/{id}")
    public Map<String, Object> updateBed(@PathVariable String id,
                                         @RequestBody Map<String, String> body) {
        Bed bed = ctx.getBedDao().findById(id);
        if (bed == null) return error("床位不存在");
        if (body.containsKey("bedNo")) bed.setBedNo(body.get("bedNo"));
        if (body.containsKey("status")) bed.setStatus(body.get("status"));
        ctx.getBedDao().update(bed);
        saveId();
        AuditLogger.log("修改床位", "床位管理", "修改床位 " + id);
        return success("修改成功");
    }

    @PutMapping("/beds/swap")
    public Map<String, Object> swapBed(@RequestBody Map<String, String> body) {
        Elderly eA = ctx.getElderlyDao().findById(body.get("elderlyIdA"));
        Elderly eB = ctx.getElderlyDao().findById(body.get("elderlyIdB"));
        if (eA == null || eB == null) return error("老人不存在");

        String bedA = eA.getBedId(), roomA = eA.getRoomNo();
        eA.setBedId(eB.getBedId()); eA.setRoomNo(eB.getRoomNo());
        eB.setBedId(bedA); eB.setRoomNo(roomA);
        ctx.getElderlyDao().update(eA);
        ctx.getElderlyDao().update(eB);

        if (eA.getBedId() != null) {
            Bed bed = ctx.getBedDao().findById(eA.getBedId());
            if (bed != null) { bed.setStatus("占用"); ctx.getBedDao().update(bed); }
        }
        if (eB.getBedId() != null) {
            Bed bed = ctx.getBedDao().findById(eB.getBedId());
            if (bed != null) { bed.setStatus("占用"); ctx.getBedDao().update(bed); }
        }
        saveId();
        AuditLogger.log("调换床位", "床位管理", "老人 " + body.get("elderlyIdA") + " 与 " + body.get("elderlyIdB") + " 调换床位");
        return success("床位调换成功");
    }
}
