package neu.YYZX.view;

import neu.YYZX.common.IMenu;
import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.model.*;
import neu.YYZX.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员菜单（五大模块 + 员工管理子菜单，共27个功能项）
 */
public class AdminMenu implements IMenu {

    private final DataInitializer ctx = DataInitializer.getInstance();

    private final UserService userService = new UserService(ctx.getUserDao());
    private final ElderlyService elderlyService = new ElderlyService(ctx.getElderlyDao());
    private final NursingLevelService levelService = new NursingLevelService(ctx.getNursingLevelDao());
    private final CareProjectService projectService = new CareProjectService(ctx.getCareProjectDao());
    private final CareRecordService recordService = new CareRecordService(ctx.getCareRecordDao());
    private final BuildingService buildingService = new BuildingService(ctx.getBuildingDao());
    private final RoomService roomService = new RoomService(ctx.getRoomDao());
    private final BedService bedService = new BedService(ctx.getBedDao());
    private final FoodService foodService = new FoodService(ctx.getFoodDao());
    private final DietCalendarService dietService = new DietCalendarService(ctx.getDietCalendarDao());
    private final OutRegistrationService outService = new OutRegistrationService(ctx.getOutRegistrationDao());
    private final CheckOutService checkOutService = new CheckOutService(ctx.getCheckOutDao());
    private final HealthRecordService healthService = new HealthRecordService(ctx.getHealthRecordDao());
    private final DietPreferenceService dietPrefService = new DietPreferenceService(ctx.getDietPreferenceDao());
    private final NursingContentService contentService = new NursingContentService(ctx.getNursingContentDao());

    @Override
    public void show() {
        System.out.println("======系统管理员登录======");
        System.out.print("请输入账号：");
        String username = mainMenu.readWord();
        System.out.print("请输入密码：");
        String password = mainMenu.readWord();

        User user = userService.authenticate(username, password, User.ROLE_ADMIN);
        if (user == null) {
            System.out.println("登录失败！账号、密码或角色不正确");
            return;
        }
        System.out.println("登录成功！欢迎，" + user.getUsername());
        adminLoop();
    }

    private void adminLoop() {
        while (true) {
            System.out.println();
            System.out.println("==========管理员功能菜单==========");
            System.out.println(" 1.  添加用户          2.  查询所有用户信息");
            System.out.println(" 3.  条件查询用户      4.  修改用户");
            System.out.println(" 5.  删除用户          6.  查询所有护理级别");
            System.out.println(" 7.  添加护理级别      8.  查询所有护理项目");
            System.out.println(" 9.  添加护理项目      10. 查询管家护理记录");
            System.out.println("11.  客户护理级别设置  12. 床位示意图");
            System.out.println("13.  添加床位          14. 修改床位");
            System.out.println("15.  调换床位          16. 入住登录");
            System.out.println("17.  外出审核          18. 退住审核");
            System.out.println("19.  查询外出信息      20. 查询退住信息");
            System.out.println("21.  管家列表          22. 设置服务对象");
            System.out.println("23.  服务关注          24. 服务续费");
            System.out.println("25.  服务购买          26. 员工管理");
            System.out.println("27.  返回主菜单");
            System.out.print("请选择：");

            switch (mainMenu.readInt()) {
                case 1:  addUser(); break;
                case 2:  listAllUsers(); break;
                case 3:  searchUser(); break;
                case 4:  updateUser(); break;
                case 5:  deleteUser(); break;
                case 6:  listAllLevels(); break;
                case 7:  addLevel(); break;
                case 8:  listAllProjects(); break;
                case 9:  addProject(); break;
                case 10: listCareRecords(); break;
                case 11: setElderlyLevel(); break;
                case 12: bedDiagram(); break;
                case 13: addBed(); break;
                case 14: updateBed(); break;
                case 15: swapBed(); break;
                case 16: checkIn(); break;
                case 17: reviewOut(); break;
                case 18: reviewCheckOut(); break;
                case 19: queryOutInfo(); break;
                case 20: queryCheckOutInfo(); break;
                case 21: listHousekeepers(); break;
                case 22: assignServiceTarget(); break;
                case 23: serviceFollowUp(); break;
                case 24: serviceRenew(); break;
                case 25: servicePurchase(); break;
                case 26: employeeManage(); break;
                case 27: return;
                default: System.out.println("输入有误，请重新输入");
            }
        }
    }

    // ==================== 1-5 用户管理 ====================

    /** 1. 添加用户 */
    private void addUser() {
        System.out.print("请输入账号：");
        String uname = mainMenu.readWord();
        if (userService.containsUsername(uname)) { System.out.println("账号已存在"); return; }
        System.out.print("请输入密码：");
        String pwd = mainMenu.readWord();
        System.out.print("请输入角色(admin/nurse)：");
        String role = mainMenu.readWord().toLowerCase();
        if (!User.ROLE_ADMIN.equals(role) && !User.ROLE_NURSE.equals(role)) {
            System.out.println("角色无效"); return;
        }
        System.out.print("请输入真实姓名：");
        String rname = mainMenu.readLine();
        System.out.print("请输入电话：");
        String phone = mainMenu.readLine();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        userService.addUser(new User(null, uname, pwd, role, rname, phone, now));
        PersistentIdGenerator.getInstance().save();
        System.out.println("添加成功！");
    }

    /** 2. 查询所有用户信息 */
    private void listAllUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) { System.out.println("暂无用户"); return; }
        System.out.println("ID\t账号\t姓名\t角色\t电话\t创建时间");
        for (User u : users) {
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    u.getUserId(), u.getUsername(),
                    nvl(u.getRealName()),
                    User.ROLE_ADMIN.equals(u.getRole()) ? "管理员" : "护工",
                    nvl(u.getPhone()), nvl(u.getCreateTime()));
        }
    }

    /** 3. 根据条件查询用户信息 */
    private void searchUser() {
        System.out.println("查询条件：1.按账号  2.按角色  3.按姓名");
        System.out.print("请选择：");
        int cond = mainMenu.readInt();
        if (cond == 1) {
            System.out.print("请输入账号：");
            String uname = mainMenu.readWord();
            User u = userService.containsUsername(uname)
                    ? ctx.getUserDao().findByUsername(uname) : null;
            if (u == null) { System.out.println("未找到"); return; }
            System.out.printf("ID:%s 账号:%s 姓名:%s 角色:%s 电话:%s%n",
                    u.getUserId(), u.getUsername(), nvl(u.getRealName()),
                    u.getRole(), nvl(u.getPhone()));
        } else if (cond == 2) {
            System.out.print("请输入角色(admin/nurse)：");
            String role = mainMenu.readWord().toLowerCase();
            List<User> users = ctx.getUserDao().findByRole(role);
            if (users.isEmpty()) { System.out.println("未找到"); return; }
            System.out.println("ID\t账号\t姓名\t电话");
            for (User u : users)
                System.out.printf("%s\t%s\t%s\t%s%n", u.getUserId(), u.getUsername(),
                        nvl(u.getRealName()), nvl(u.getPhone()));
        } else if (cond == 3) {
            System.out.print("请输入姓名关键字：");
            String kw = mainMenu.readLine();
            List<User> users = userService.findAll();
            boolean found = false;
            for (User u : users) {
                if (u.getRealName() != null && u.getRealName().contains(kw)) {
                    System.out.printf("ID:%s 账号:%s 姓名:%s 角色:%s 电话:%s%n",
                            u.getUserId(), u.getUsername(), u.getRealName(),
                            u.getRole(), nvl(u.getPhone()));
                    found = true;
                }
            }
            if (!found) System.out.println("未找到");
        } else {
            System.out.println("条件无效");
        }
    }

    /** 4. 修改用户 */
    private void updateUser() {
        System.out.print("请输入要修改的账号：");
        String uname = mainMenu.readWord();
        User u = ctx.getUserDao().findByUsername(uname);
        if (u == null) { System.out.println("账号不存在"); return; }
        System.out.print("新密码(回车不变)：");
        String pwd = mainMenu.readLine();
        if (!pwd.isEmpty()) u.setPassword(pwd);
        System.out.print("新姓名(" + nvl(u.getRealName()) + ")：");
        String rn = mainMenu.readLine();
        if (!rn.isEmpty()) u.setRealName(rn);
        System.out.print("新电话(" + nvl(u.getPhone()) + ")：");
        String ph = mainMenu.readLine();
        if (!ph.isEmpty()) u.setPhone(ph);
        System.out.print("新角色(" + u.getRole() + ")：");
        String role = mainMenu.readLine();
        if (!role.isEmpty()) u.setRole(role.toLowerCase());
        ctx.getUserDao().update(u);
        PersistentIdGenerator.getInstance().save();
        System.out.println("修改成功！");
    }

    /** 5. 删除用户 */
    private void deleteUser() {
        System.out.print("请输入要删除的账号：");
        String uname = mainMenu.readWord();
        String err = userService.deleteUser(uname);
        System.out.println(err != null ? err : "删除成功！");
        PersistentIdGenerator.getInstance().save();
    }

    // ==================== 6-11 护理管理 ====================

    /** 6. 查询所有护理级别 */
    private void listAllLevels() {
        List<NursingLevel> list = levelService.findAll();
        if (list.isEmpty()) { System.out.println("暂无数据"); return; }
        System.out.println("编码\t名称\t巡查频次\t描述");
        for (NursingLevel l : list)
            System.out.printf("%s\t%s\t%s\t%s%n",
                    l.getCode(), l.getName(), l.getFrequency(), l.getDescription());
    }

    /** 7. 添加护理级别 */
    private void addLevel() {
        System.out.print("编码：");
        String code = mainMenu.readWord();
        if (levelService.exists(code)) { System.out.println("编码已存在"); return; }
        System.out.print("名称：");
        String name = mainMenu.readLine();
        System.out.print("描述：");
        String desc = mainMenu.readLine();
        System.out.print("巡查频次：");
        String freq = mainMenu.readLine();
        levelService.add(new NursingLevel(code, name, desc, freq));
        PersistentIdGenerator.getInstance().save();
        System.out.println("添加成功！");
    }

    /** 8. 查询所有护理项目 */
    private void listAllProjects() {
        List<CareProject> list = projectService.findAll();
        if (list.isEmpty()) { System.out.println("暂无数据"); return; }
        System.out.println("编码\t名称\t类别\t单位\t价格\t周期\t备注");
        for (CareProject p : list)
            System.out.printf("%s\t%s\t%s\t%s\t%.0f\t%s\t%s%n",
                    p.getCode(), p.getName(), p.getCategory(),
                    p.getUnit(), p.getPrice(), p.getCycle(), p.getRemark());
    }

    /** 9. 添加护理项目 */
    private void addProject() {
        System.out.print("编码：");
        String code = mainMenu.readWord();
        if (projectService.exists(code)) { System.out.println("编码已存在"); return; }
        System.out.print("名称：");
        String name = mainMenu.readLine();
        System.out.print("类别：");
        String cat = mainMenu.readLine();
        System.out.print("单位：");
        String unit = mainMenu.readWord();
        System.out.print("价格：");
        double price = mainMenu.readDouble();
        System.out.print("周期：");
        String cycle = mainMenu.readLine();
        System.out.print("备注/适用等级：");
        String remark = mainMenu.readLine();
        projectService.add(new CareProject(code, name, cat, unit, price, cycle, remark));
        PersistentIdGenerator.getInstance().save();
        System.out.println("添加成功！");
    }

    /** 10. 查询管家护理记录 */
    private void listCareRecords() {
        List<CareRecord> list = recordService.findAll();
        if (list.isEmpty()) { System.out.println("暂无护理记录"); return; }
        System.out.println("记录号\t老人编号\t项目编码\t执行时间\t护工\t备注");
        for (CareRecord r : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    r.getId(), r.getElderlyId(), r.getProjectCode(),
                    r.getExecuteTime(), r.getNurseName(), nvl(r.getRemark()));
    }

    /** 11. 客户护理级别设置 */
    private void setElderlyLevel() {
        System.out.print("请输入老人编号：");
        String eid = mainMenu.readWord();
        Elderly e = elderlyService.findById(eid);
        if (e == null) { System.out.println("老人不存在"); return; }
        System.out.println("当前护理等级：" + e.getNursingLevelCode());

        List<NursingLevel> levels = levelService.findAll();
        System.out.println("可选护理等级：");
        for (NursingLevel l : levels)
            System.out.printf("  %s - %s (%s)%n", l.getCode(), l.getName(), l.getFrequency());
        System.out.print("请输入新护理等级编码：");
        String newLevel = mainMenu.readWord();
        if (!levelService.exists(newLevel)) { System.out.println("护理等级不存在"); return; }
        e.setNursingLevelCode(newLevel);
        elderlyService.update(e);
        PersistentIdGenerator.getInstance().save();
        System.out.println("护理等级设置成功！");
    }

    // ==================== 12-15 床位管理 ====================

    /** 12. 床位示意图 */
    private void bedDiagram() {
        List<Room> rooms = roomService.findAll();
        if (rooms.isEmpty()) { System.out.println("暂无房间数据"); return; }
        List<Bed> beds = bedService.findAll();

        for (Room room : rooms) {
            Building building = buildingService.findById(room.getBuildingId());
            String bldName = building != null ? building.getBuildingName() : "未知楼栋";
            System.out.println();
            System.out.println("[" + bldName + " " + room.getFloor() + "F " + room.getRoomNo()
                    + " (" + room.getRoomType() + " ￥" + (int) room.getPrice() + "/月)]");
            List<Bed> roomBeds = new ArrayList<>();
            for (Bed b : beds) {
                if (b.getRoomId().equals(room.getRoomId())) roomBeds.add(b);
            }
            if (roomBeds.isEmpty()) {
                System.out.println("  (无床位)");
                continue;
            }
            for (Bed b : roomBeds) {
                String icon = "空闲".equals(b.getStatus()) ? "○" :
                              "占用".equals(b.getStatus()) ? "●" : "×";
                Elderly occupant = ctx.getElderlyDao().findByBedId(b.getBedId());
                String occName = occupant != null ? occupant.getName() : "";
                System.out.printf("  %s 床位%s [%s] %s%n", icon, b.getBedNo(), b.getStatus(), occName);
            }
        }
    }

    /** 13. 添加床位 */
    private void addBed() {
        System.out.print("床位号：");
        String bn = mainMenu.readWord();
        System.out.print("房间ID：");
        String rid = mainMenu.readWord();
        if (!roomService.exists(rid)) { System.out.println("房间不存在"); return; }
        bedService.add(new Bed(null, bn, rid, "空闲"));
        PersistentIdGenerator.getInstance().save();
        System.out.println("添加成功！");
    }

    /** 14. 修改床位 */
    private void updateBed() {
        System.out.print("床位ID：");
        String bid = mainMenu.readWord();
        Bed bed = bedService.findById(bid);
        if (bed == null) { System.out.println("床位不存在"); return; }
        System.out.print("新床位号(" + bed.getBedNo() + ")：");
        String bn = mainMenu.readLine();
        if (!bn.isEmpty()) bed.setBedNo(bn);
        System.out.print("新状态(空闲/占用/维修)(" + bed.getStatus() + ")：");
        String st = mainMenu.readLine();
        if (!st.isEmpty()) bed.setStatus(st);
        bedService.update(bed);
        PersistentIdGenerator.getInstance().save();
        System.out.println("修改成功！");
    }

    /** 15. 调换床位 */
    private void swapBed() {
        System.out.print("请输入老人A编号：");
        String idA = mainMenu.readWord();
        Elderly eA = elderlyService.findById(idA);
        if (eA == null) { System.out.println("老人A不存在"); return; }
        System.out.print("请输入老人B编号：");
        String idB = mainMenu.readWord();
        Elderly eB = elderlyService.findById(idB);
        if (eB == null) { System.out.println("老人B不存在"); return; }

        String bedA = eA.getBedId();
        String roomA = eA.getRoomNo();
        eA.setBedId(eB.getBedId());
        eA.setRoomNo(eB.getRoomNo());
        eB.setBedId(bedA);
        eB.setRoomNo(roomA);

        elderlyService.update(eA);
        elderlyService.update(eB);

        // 更新床位状态
        updateBedOccupancy(eA.getBedId());
        updateBedOccupancy(eB.getBedId());

        PersistentIdGenerator.getInstance().save();
        System.out.println("床位调换成功！" + eA.getName() + " ↔ " + eB.getName());
    }

    private void updateBedOccupancy(String bedId) {
        if (bedId == null) return;
        Bed bed = bedService.findById(bedId);
        if (bed != null) {
            Elderly occupant = ctx.getElderlyDao().findByBedId(bedId);
            bed.setStatus(occupant != null ? "占用" : "空闲");
            bedService.update(bed);
        }
    }

    // ==================== 16-20 客户管理 ====================

    /** 16. 入住登录 */
    private void checkIn() {
        System.out.println("---老人入住登记---");
        System.out.print("姓名：");
        String name = mainMenu.readLine();
        System.out.print("年龄：");
        int age = mainMenu.readInt();
        System.out.print("性别：");
        String gender = mainMenu.readWord();
        System.out.print("身份证号：");
        String idCard = mainMenu.readLine();
        System.out.print("电话：");
        String phone = mainMenu.readLine();
        System.out.print("住址：");
        String addr = mainMenu.readLine();
        System.out.print("紧急联系人：");
        String contact = mainMenu.readLine();
        System.out.print("紧急联系电话：");
        String ephone = mainMenu.readLine();
        System.out.print("入住日期(yyyy-MM-dd)：");
        String checkin = mainMenu.readLine();

        System.out.println("可选护理等级：");
        List<NursingLevel> levels = levelService.findAll();
        for (NursingLevel l : levels)
            System.out.printf("  %s - %s%n", l.getCode(), l.getName());
        System.out.print("护理等级编码：");
        String level = mainMenu.readWord();
        if (!levelService.exists(level)) { System.out.println("护理等级不存在"); return; }

        System.out.println("可选房间/床位：");
        List<Room> rooms = roomService.findAvailable();
        for (Room r : rooms) {
            List<Bed> availBeds = bedService.findByRoomId(r.getRoomId());
            for (Bed b : availBeds) {
                if ("空闲".equals(b.getStatus()))
                    System.out.printf("  房间%s 床位%s (%s %.0f元/月)%n",
                            r.getRoomNo(), b.getBedNo(), r.getRoomType(), r.getPrice());
            }
        }
        System.out.print("房间号：");
        String roomNo = mainMenu.readWord();
        System.out.print("床位ID(无可回车跳过)：");
        String bedId = mainMenu.readLine();
        if (bedId.isEmpty()) bedId = null;
        if (bedId != null && !bedService.exists(bedId)) {
            System.out.println("床位不存在"); return;
        }

        Elderly e = new Elderly(null, name, age, gender, idCard, phone, addr,
                contact, ephone, checkin, bedId, level, roomNo, "在住");
        elderlyService.add(e);

        if (bedId != null) {
            Bed bed = bedService.findById(bedId);
            if (bed != null) { bed.setStatus("占用"); bedService.update(bed); }
        }
        PersistentIdGenerator.getInstance().save();
        System.out.println("入住登记成功！编号：" + e.getId());
    }

    /** 17. 外出审核 */
    private void reviewOut() {
        List<OutRegistration> list = outService.findByStatus("外出中");
        if (list.isEmpty()) { System.out.println("暂无待审核的外出记录"); return; }
        System.out.println("ID\t老人ID\t外出时间\t预计归来\t陪同人\t事由");
        for (OutRegistration o : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    o.getOutId(), o.getCustomerId(), o.getOutTime(),
                    o.getExpectedReturnTime(), nvl(o.getCompanion()), nvl(o.getReason()));
        System.out.println();
        System.out.println("1. 登记归来  2. 标记超时  3. 返回");
        System.out.print("请选择：");
        int act = mainMenu.readInt();
        if (act == 1) {
            System.out.print("外出登记ID：");
            String oid = mainMenu.readWord();
            OutRegistration o = outService.findById(oid);
            if (o == null) { System.out.println("记录不存在"); return; }
            System.out.print("实际归来时间：");
            o.setActualReturnTime(mainMenu.readLine());
            o.setStatus("已归来");
            outService.update(o);
            PersistentIdGenerator.getInstance().save();
            System.out.println("登记成功！");
        } else if (act == 2) {
            System.out.print("外出登记ID：");
            String oid = mainMenu.readWord();
            OutRegistration o = outService.findById(oid);
            if (o == null) { System.out.println("记录不存在"); return; }
            o.setStatus("超时未归");
            outService.update(o);
            PersistentIdGenerator.getInstance().save();
            System.out.println("已标记超时！");
        }
    }

    /** 18. 退住审核 */
    private void reviewCheckOut() {
        List<CheckOut> list = checkOutService.findAll();
        if (list.isEmpty()) { System.out.println("暂无退住记录"); return; }
        System.out.println("ID\t老人ID\t退住日期\t原因\t备注");
        for (CheckOut c : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s%n",
                    c.getCheckoutId(), c.getCustomerId(), c.getCheckoutDate(),
                    nvl(c.getReason()), nvl(c.getRemark()));
        System.out.println();
        System.out.println("1. 确认退住(释放床位)  2. 撤销退住  3. 返回");
        System.out.print("请选择：");
        int act = mainMenu.readInt();
        if (act == 1) {
            System.out.print("退住登记ID：");
            String cid = mainMenu.readWord();
            CheckOut c = checkOutService.findById(cid);
            if (c == null) { System.out.println("记录不存在"); return; }
            Elderly e = elderlyService.findById(c.getCustomerId());
            if (e != null) {
                if (e.getBedId() != null) {
                    Bed bed = bedService.findById(e.getBedId());
                    if (bed != null) { bed.setStatus("空闲"); bedService.update(bed); }
                }
                e.setStatus("退住");
                elderlyService.update(e);
            }
            PersistentIdGenerator.getInstance().save();
            System.out.println("退住确认成功，床位已释放！");
        } else if (act == 2) {
            System.out.print("退住登记ID：");
            String cid = mainMenu.readWord();
            checkOutService.delete(cid);
            PersistentIdGenerator.getInstance().save();
            System.out.println("退住记录已撤销！");
        }
    }

    /** 19. 查询外出信息 */
    private void queryOutInfo() {
        System.out.println("1. 查看全部  2. 按老人查询  3. 按状态查询");
        System.out.print("请选择：");
        int c = mainMenu.readInt();
        List<OutRegistration> list;
        if (c == 2) {
            System.out.print("老人编号：");
            list = outService.findByCustomerId(mainMenu.readWord());
        } else if (c == 3) {
            System.out.print("状态(外出中/已归来/超时未归)：");
            list = outService.findByStatus(mainMenu.readWord());
        } else {
            list = outService.findAll();
        }
        if (list.isEmpty()) { System.out.println("暂无记录"); return; }
        System.out.println("ID\t老人ID\t外出时间\t预计归来\t实际归来\t状态");
        for (OutRegistration o : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    o.getOutId(), o.getCustomerId(), o.getOutTime(),
                    o.getExpectedReturnTime(), nvl(o.getActualReturnTime()), o.getStatus());
    }

    /** 20. 查询退住信息 */
    private void queryCheckOutInfo() {
        System.out.println("1. 查看全部  2. 按老人查询");
        System.out.print("请选择：");
        int c = mainMenu.readInt();
        List<CheckOut> list;
        if (c == 2) {
            System.out.print("老人编号：");
            list = checkOutService.findByCustomerId(mainMenu.readWord());
        } else {
            list = checkOutService.findAll();
        }
        if (list.isEmpty()) { System.out.println("暂无记录"); return; }
        System.out.println("ID\t老人ID\t退住日期\t原因\t备注");
        for (CheckOut co : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s%n",
                    co.getCheckoutId(), co.getCustomerId(), co.getCheckoutDate(),
                    nvl(co.getReason()), nvl(co.getRemark()));
    }

    // ==================== 21-25 健康管家管理 ====================

    /** 21. 管家列表 */
    private void listHousekeepers() {
        List<Employee> all = ctx.getEmployeeDao().findByPosition("管家");
        if (all.isEmpty()) {
            // 也尝试列出所有在职员工
            all = ctx.getEmployeeDao().findAll();
            if (all.isEmpty()) { System.out.println("暂无管家/员工数据，请先在员工管理中添加"); return; }
            System.out.println("(显示所有员工，其中职位为[管家]的是管家)");
        }
        System.out.println("ID\t姓名\t性别\t职位\t电话\t状态");
        for (Employee emp : all)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    emp.getEmployeeId(), emp.getName(), emp.getGender(),
                    emp.getPosition(), emp.getPhone(), emp.getStatus());
    }

    /** 22. 设置服务对象 */
    private void assignServiceTarget() {
        System.out.print("请输入管家ID：");
        String empId = mainMenu.readWord();
        Employee emp = ctx.getEmployeeDao().findById(empId);
        if (emp == null) { System.out.println("管家不存在"); return; }
        System.out.print("请输入老人编号：");
        String eid = mainMenu.readWord();
        if (!elderlyService.exists(eid)) { System.out.println("老人不存在"); return; }
        System.out.print("服务类型：");
        String type = mainMenu.readLine();
        System.out.print("开始日期(yyyy-MM-dd)：");
        String start = mainMenu.readLine();
        System.out.print("结束日期(yyyy-MM-dd)：");
        String end = mainMenu.readLine();
        System.out.print("费用：");
        double fee = mainMenu.readDouble();
        System.out.print("备注：");
        String remark = mainMenu.readLine();

        ctx.getServiceAssignmentDao().insert(
                new ServiceAssignment(null, empId, eid, type, start, end, fee, "服务中", remark));
        PersistentIdGenerator.getInstance().save();
        System.out.println("服务对象设置成功！");
    }

    /** 23. 服务关注 */
    private void serviceFollowUp() {
        List<ServiceAssignment> list = ctx.getServiceAssignmentDao().findByStatus("服务中");
        if (list.isEmpty()) { System.out.println("暂无服务中的记录"); return; }
        System.out.println("ID\t管家ID\t老人ID\t服务类型\t开始\t结束\t费用");
        for (ServiceAssignment sa : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%.0f%n",
                    sa.getAssignmentId(), sa.getEmployeeId(), sa.getElderlyId(),
                    sa.getServiceType(), sa.getStartDate(), sa.getEndDate(), sa.getFee());
        System.out.println();
        System.out.print("输入要关注的分配ID(回车跳过)：");
        String aid = mainMenu.readLine();
        if (aid.isEmpty()) return;
        ServiceAssignment sa = ctx.getServiceAssignmentDao().findById(aid);
        if (sa == null) { System.out.println("记录不存在"); return; }
        System.out.print("添加关注备注：");
        String note = mainMenu.readLine();
        String oldRemark = nvl(sa.getRemark());
        String newRemark = oldRemark + (oldRemark.isEmpty() ? "" : "; ") + "[关注]" + note;
        sa.setRemark(newRemark);
        ctx.getServiceAssignmentDao().update(sa);
        PersistentIdGenerator.getInstance().save();
        System.out.println("关注备注已添加！");
    }

    /** 24. 服务续费 */
    private void serviceRenew() {
        List<ServiceAssignment> list = ctx.getServiceAssignmentDao().findByStatus("已到期");
        // 也显示即将到期的服务中记录
        List<ServiceAssignment> active = ctx.getServiceAssignmentDao().findByStatus("服务中");
        List<ServiceAssignment> all = new ArrayList<>(active);
        all.addAll(list);

        if (all.isEmpty()) { System.out.println("暂无服务记录"); return; }
        System.out.println("ID\t管家ID\t老人ID\t服务类型\t结束日期\t费用\t状态");
        for (ServiceAssignment sa : all)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%.0f\t%s%n",
                    sa.getAssignmentId(), sa.getEmployeeId(), sa.getElderlyId(),
                    sa.getServiceType(), sa.getEndDate(), sa.getFee(), sa.getStatus());
        System.out.println();
        System.out.print("输入要续费的分配ID：");
        String aid = mainMenu.readLine();
        if (aid.isEmpty()) return;
        ServiceAssignment sa = ctx.getServiceAssignmentDao().findById(aid);
        if (sa == null) { System.out.println("记录不存在"); return; }
        System.out.print("续费金额：");
        double fee = mainMenu.readDouble();
        System.out.print("延长至日期(yyyy-MM-dd)：");
        String newEnd = mainMenu.readLine();
        sa.setEndDate(newEnd);
        sa.setFee(sa.getFee() + fee);
        sa.setStatus("已续费");
        ctx.getServiceAssignmentDao().update(sa);
        PersistentIdGenerator.getInstance().save();
        System.out.println("续费成功！");
    }

    /** 25. 服务购买 */
    private void servicePurchase() {
        System.out.println("---服务购买（新建管家服务）---");
        System.out.println("可选管家：");
        List<Employee> emps = ctx.getEmployeeDao().findByPosition("管家");
        if (emps.isEmpty()) emps = ctx.getEmployeeDao().findAll();
        for (Employee emp : emps)
            System.out.printf("  %s %s (%s)%n", emp.getEmployeeId(), emp.getName(), emp.getPosition());
        System.out.print("请输入管家ID：");
        String empId = mainMenu.readWord();
        Employee emp = ctx.getEmployeeDao().findById(empId);
        if (emp == null) { System.out.println("管家不存在"); return; }

        System.out.print("老人编号：");
        String eid = mainMenu.readWord();
        if (!elderlyService.exists(eid)) { System.out.println("老人不存在"); return; }

        System.out.print("服务类型：");
        String type = mainMenu.readLine();
        System.out.print("开始日期(yyyy-MM-dd)：");
        String start = mainMenu.readLine();
        System.out.print("结束日期(yyyy-MM-dd)：");
        String end = mainMenu.readLine();
        System.out.print("费用：");
        double fee = mainMenu.readDouble();
        System.out.print("备注：");
        String remark = mainMenu.readLine();

        ctx.getServiceAssignmentDao().insert(
                new ServiceAssignment(null, empId, eid, type, start, end, fee, "服务中", remark));
        PersistentIdGenerator.getInstance().save();
        System.out.println("服务购买成功！");
    }

    // ==================== 26. 员工管理 ====================

    private void employeeManage() {
        while (true) {
            System.out.println();
            System.out.println("---员工管理---");
            System.out.println("1. 添加员工  2. 查询所有员工  3. 条件查询  4. 修改员工  5. 删除员工  6. 返回");
            System.out.print("请选择：");
            switch (mainMenu.readInt()) {
                case 1: addEmployee(); break;
                case 2: listAllEmployees(); break;
                case 3: searchEmployee(); break;
                case 4: updateEmployee(); break;
                case 5: deleteEmployee(); break;
                case 6: return;
                default: System.out.println("输入有误");
            }
        }
    }

    private void addEmployee() {
        System.out.print("姓名：");
        String name = mainMenu.readLine();
        System.out.print("性别：");
        String gender = mainMenu.readWord();
        System.out.print("职位(护工/管家/医生/行政)：");
        String pos = mainMenu.readWord();
        System.out.print("电话：");
        String phone = mainMenu.readLine();
        System.out.print("身份证号：");
        String idCard = mainMenu.readLine();
        System.out.print("入职日期(yyyy-MM-dd)：");
        String hire = mainMenu.readLine();
        System.out.print("薪资：");
        double salary = mainMenu.readDouble();
        System.out.print("备注：");
        String remark = mainMenu.readLine();
        ctx.getEmployeeDao().insert(
                new Employee(null, name, gender, pos, phone, idCard, hire, salary, "在职", remark));
        PersistentIdGenerator.getInstance().save();
        System.out.println("添加成功！");
    }

    private void listAllEmployees() {
        List<Employee> list = ctx.getEmployeeDao().findAll();
        if (list.isEmpty()) { System.out.println("暂无员工"); return; }
        System.out.println("ID\t姓名\t性别\t职位\t电话\t入职日期\t薪资\t状态");
        for (Employee e : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%.0f\t%s%n",
                    e.getEmployeeId(), e.getName(), e.getGender(), e.getPosition(),
                    e.getPhone(), e.getHireDate(), e.getSalary(), e.getStatus());
    }

    private void searchEmployee() {
        System.out.println("1. 按ID  2. 按姓名  3. 按职位");
        System.out.print("请选择：");
        int c = mainMenu.readInt();
        List<Employee> list = new ArrayList<>();
        if (c == 1) {
            Employee e = ctx.getEmployeeDao().findById(mainMenu.readWord());
            if (e != null) list.add(e);
        } else if (c == 2) {
            list = ctx.getEmployeeDao().findByName(mainMenu.readLine());
        } else if (c == 3) {
            list = ctx.getEmployeeDao().findByPosition(mainMenu.readWord());
        } else { System.out.println("条件无效"); return; }
        if (list.isEmpty()) { System.out.println("未找到"); return; }
        System.out.println("ID\t姓名\t性别\t职位\t电话\t状态");
        for (Employee e : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    e.getEmployeeId(), e.getName(), e.getGender(),
                    e.getPosition(), e.getPhone(), e.getStatus());
    }

    private void updateEmployee() {
        System.out.print("员工ID：");
        String id = mainMenu.readWord();
        Employee e = ctx.getEmployeeDao().findById(id);
        if (e == null) { System.out.println("员工不存在"); return; }
        System.out.print("新姓名(" + e.getName() + ")：");
        String v = mainMenu.readLine(); if (!v.isEmpty()) e.setName(v);
        System.out.print("新职位(" + e.getPosition() + ")：");
        v = mainMenu.readLine(); if (!v.isEmpty()) e.setPosition(v);
        System.out.print("新电话(" + nvl(e.getPhone()) + ")：");
        v = mainMenu.readLine(); if (!v.isEmpty()) e.setPhone(v);
        System.out.print("新薪资(" + e.getSalary() + ")：");
        String sv = mainMenu.readLine(); if (!sv.isEmpty()) e.setSalary(Double.parseDouble(sv));
        System.out.print("新状态(" + e.getStatus() + ")：");
        v = mainMenu.readLine(); if (!v.isEmpty()) e.setStatus(v);
        ctx.getEmployeeDao().update(e);
        PersistentIdGenerator.getInstance().save();
        System.out.println("修改成功！");
    }

    private void deleteEmployee() {
        System.out.print("员工ID：");
        String id = mainMenu.readWord();
        if (ctx.getEmployeeDao().delete(id)) {
            PersistentIdGenerator.getInstance().save();
            System.out.println("删除成功！");
        } else {
            System.out.println("员工不存在");
        }
    }

    // ==================== 工具方法 ====================
    private static String nvl(String s) { return s == null ? "" : s; }
}
