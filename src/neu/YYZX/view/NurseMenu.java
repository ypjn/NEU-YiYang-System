package neu.YYZX.view;

import neu.YYZX.common.IMenu;
import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.model.*;
import neu.YYZX.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 护工菜单
 */
public class NurseMenu implements IMenu {

    private final DataInitializer ctx = DataInitializer.getInstance();

    private final UserService userService = new UserService(ctx.getUserDao());
    private final ElderlyService elderlyService = new ElderlyService(ctx.getElderlyDao());
    private final CareProjectService projectService = new CareProjectService(ctx.getCareProjectDao());
    private final CareRecordService recordService = new CareRecordService(ctx.getCareRecordDao());
    private final OutRegistrationService outService = new OutRegistrationService(ctx.getOutRegistrationDao());
    private final HealthRecordService healthService = new HealthRecordService(ctx.getHealthRecordDao());
    private final NursingLevelService levelService = new NursingLevelService(ctx.getNursingLevelDao());

    private String nurseName;

    @Override
    public void show() {
        System.out.println("======护工登录======");
        System.out.print("请输入账号：");
        String username = mainMenu.readWord();
        System.out.print("请输入密码：");
        String password = mainMenu.readWord();

        User user = userService.authenticate(username, password, User.ROLE_NURSE);
        if (user == null) {
            System.out.println("登录失败！账号、密码或角色不正确");
            return;
        }

        nurseName = user.getUsername();
        System.out.println("登录成功！欢迎，" + nurseName);
        nurseLoop();
    }

    private void nurseLoop() {
        while (true) {
            System.out.println();
            System.out.println("==========护工功能菜单==========");
            System.out.println("1. 查询老人信息");
            System.out.println("2. 登记护理执行");
            System.out.println("3. 查看我的护理记录");
            System.out.println("4. 外出登记");
            System.out.println("5. 登记健康记录");
            System.out.println("6. 返回主菜单");
            System.out.print("请选择：");

            switch (mainMenu.readInt()) {
                case 1: searchElderly(); break;
                case 2: registerCare(); break;
                case 3: listMyRecords(); break;
                case 4: outRegistration(); break;
                case 5: healthRecord(); break;
                case 6: return;
                default: System.out.println("输入有误，请重新输入");
            }
        }
    }

    /** 1. 查询老人信息 */
    private void searchElderly() {
        System.out.print("请输入老人编号或姓名关键字：");
        String keyword = mainMenu.readWord().trim();
        if (keyword.isEmpty()) { System.out.println("关键字不能为空"); return; }

        List<Elderly> matched = elderlyService.findByName(keyword);
        Elderly byId = elderlyService.findById(keyword);
        if (byId != null && !matched.contains(byId)) {
            matched.add(0, byId);
        }

        if (matched.isEmpty()) { System.out.println("未找到匹配的老人信息"); return; }

        System.out.println("编号\t姓名\t年龄\t性别\t护理等级\t等级说明\t房间号\t状态");
        for (Elderly e : matched) {
            NursingLevel level = levelService.findByCode(e.getNursingLevelCode());
            System.out.printf("%s\t%s\t%d\t%s\t%s\t%s\t%s\t%s%n",
                    e.getId(), e.getName(), e.getAge(), e.getGender(),
                    e.getNursingLevelCode(), level != null ? level.getName() : "未知",
                    e.getRoomNo(), nvl(e.getStatus()));
        }
    }

    /** 2. 登记护理执行 */
    private void registerCare() {
        System.out.print("请输入老人编号：");
        String elderId = mainMenu.readWord();
        Elderly elder = elderlyService.findById(elderId);
        if (elder == null) { System.out.println("老人编号不存在"); return; }

        List<CareProject> applicable = projectService.findApplicable(elder.getNursingLevelCode());
        if (applicable.isEmpty()) { System.out.println("当前护理等级无适用项目"); return; }

        System.out.println("可选护理项目：");
        for (int i = 0; i < applicable.size(); i++) {
            CareProject p = applicable.get(i);
            System.out.printf("%d. %s %s (%.0f元/%s)%n",
                    i + 1, p.getCode(), p.getName(), p.getPrice(), p.getUnit());
        }
        System.out.print("请选择项目序号：");
        int idx = mainMenu.readInt() - 1;
        if (idx < 0 || idx >= applicable.size()) { System.out.println("选择无效"); return; }

        CareProject project = applicable.get(idx);
        mainMenu.getScanner().nextLine();
        System.out.print("备注(可回车跳过)：");
        String remark = mainMenu.getScanner().nextLine();

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        CareRecord record = new CareRecord(null, elderId, project.getCode(), time, nurseName, remark);
        recordService.add(record);
        PersistentIdGenerator.getInstance().save();
        System.out.println("护理执行登记成功！记录号：" + record.getId());
    }

    /** 3. 查看我的护理记录 */
    private void listMyRecords() {
        List<CareRecord> list = recordService.findByNurseName(nurseName);
        if (list.isEmpty()) { System.out.println("暂无本人执行的护理记录"); return; }

        System.out.println("记录号\t老人编号\t项目编码\t执行时间\t护工\t备注");
        for (CareRecord r : list)
            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                    r.getId(), r.getElderlyId(), r.getProjectCode(),
                    r.getExecuteTime(), r.getNurseName(),
                    r.getRemark() == null ? "" : r.getRemark());
    }

    /** 4. 外出登记 */
    private void outRegistration() {
        System.out.print("请输入老人编号：");
        String elderId = mainMenu.readWord();
        Elderly elder = elderlyService.findById(elderId);
        if (elder == null) { System.out.println("老人编号不存在"); return; }

        mainMenu.getScanner().nextLine();
        System.out.print("外出时间(yyyy-MM-dd HH:mm)：");
        String outTime = mainMenu.getScanner().nextLine();
        System.out.print("预计归来时间：");
        String expected = mainMenu.getScanner().nextLine();
        System.out.print("陪同人：");
        String companion = mainMenu.getScanner().nextLine();
        System.out.print("事由：");
        String reason = mainMenu.getScanner().nextLine();

        outService.add(new OutRegistration(null, elderId, outTime, expected, null, companion, reason, "外出中"));
        PersistentIdGenerator.getInstance().save();
        System.out.println("外出登记成功！");
    }

    /** 5. 登记健康记录 */
    private void healthRecord() {
        System.out.print("请输入老人编号：");
        String elderId = mainMenu.readWord();
        if (!elderlyService.exists(elderId)) { System.out.println("老人编号不存在"); return; }

        mainMenu.getScanner().nextLine();
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.print("记录日期(" + today + ")：");
        String date = mainMenu.getScanner().nextLine();
        if (date.isEmpty()) date = today;
        System.out.print("血压："); String bp = mainMenu.getScanner().nextLine();
        System.out.print("心率："); String hr = mainMenu.getScanner().nextLine();
        System.out.print("血糖："); String bs = mainMenu.getScanner().nextLine();
        System.out.print("体重："); String wt = mainMenu.getScanner().nextLine();
        System.out.print("体温："); String tp = mainMenu.getScanner().nextLine();
        System.out.print("备注："); String remark = mainMenu.getScanner().nextLine();

        healthService.add(new HealthRecord(null, elderId, date, bp, hr, bs, wt, tp, remark));
        PersistentIdGenerator.getInstance().save();
        System.out.println("健康记录登记成功！");
    }

    private static String nvl(String s) { return s == null ? "" : s; }
}
