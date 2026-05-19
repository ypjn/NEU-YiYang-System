package neu.YYZX.view;

import neu.YYZX.model.CareProject;
import neu.YYZX.model.CareRecord;
import neu.YYZX.model.Elderly;
import neu.YYZX.model.NursingLevel;
import neu.YYZX.service.DataManager;
import neu.YYZX.util.FileUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NurseMenu {
    private final DataManager dm = DataManager.getInstance();
    private final Scanner sc;
    private String nurseName;

    public NurseMenu(Scanner sc) {
        this.sc = sc;
    }

    public void execute() {
        System.out.println("======护工登录======");
        System.out.println("请输入账号：");
        String userName = sc.next();
        System.out.println("请输入密码：");
        String password = sc.next();

        if (!userName.equalsIgnoreCase("nurse") || !password.equalsIgnoreCase("nurse")) {
            System.out.println("登录失败！");
            return;
        }

        nurseName = userName;
        System.out.println("登录成功！");
        nurseLoop();
    }

    private void nurseLoop() {
        while (true) {
            System.out.println();
            System.out.println("==========护工功能菜单==========");
            System.out.println("1---------查询老人信息");
            System.out.println("2---------登记护理执行");
            System.out.println("3---------查看护理执行记录");
            System.out.println("4---------返回主菜单");
            System.out.print("请选择：");

            int choice = readInt();
            switch (choice) {
                case 1:
                    searchElder();
                    break;
                case 2:
                    registerCare();
                    break;
                case 3:
                    listMyRecords();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    private void searchElder() {
        System.out.print("请输入老人编号或姓名关键字：");
        String keyword = sc.next().trim();
        if (keyword.isEmpty()) {
            System.out.println("关键字不能为空");
            return;
        }

        ArrayList<Elderly> matched = new ArrayList<>();
        for (Elderly e : dm.getElders()) {
            if (e.getId().contains(keyword) || e.getName().contains(keyword)) {
                matched.add(e);
            }
        }

        if (matched.isEmpty()) {
            System.out.println("未找到匹配的老人信息");
            return;
        }

        System.out.println("编号\t姓名\t年龄\t性别\t护理等级\t房间号\t等级说明");
        for (Elderly e : matched) {
            NursingLevel level = dm.findLevelByCode(e.getNursingLevelCode());
            String levelName = level != null ? level.getName() : "未知";
            System.out.printf("%s\t%s\t%d\t%s\t%s\t%s\t%s%n",
                    e.getId(), e.getName(), e.getAge(), e.getGender(),
                    e.getNursingLevelCode(), e.getRoomNo(), levelName);
        }
    }

    private void registerCare() {
        System.out.print("请输入老人编号：");
        String elderId = sc.next();
        Elderly elder = dm.findElderById(elderId);
        if (elder == null) {
            System.out.println("老人编号不存在，请重新输入");
            return;
        }

        List<CareProject> applicable = dm.getApplicableProjects(elder.getNursingLevelCode());
        if (applicable.isEmpty()) {
            System.out.println("当前护理等级无适用项目，请联系管理员维护项目库");
            return;
        }

        System.out.println("可选护理项目：");
        for (int i = 0; i < applicable.size(); i++) {
            CareProject p = applicable.get(i);
            System.out.printf("%d. %s %s (%.0f元/%s) %s%n",
                    i + 1, p.getCode(), p.getName(), p.getPrice(), p.getUnit(), p.getRemark());
        }
        System.out.print("请选择项目序号：");
        int index = readInt() - 1;
        if (index < 0 || index >= applicable.size()) {
            System.out.println("选择无效");
            return;
        }

        CareProject project = applicable.get(index);
        sc.nextLine();
        System.out.print("请输入备注(可回车跳过)：");
        String remark = sc.nextLine();

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        CareRecord record = new CareRecord(
                dm.nextRecordId(),
                elderId,
                project.getCode(),
                time,
                nurseName,
                remark);
        dm.getRecords().add(record);
        FileUtil.persist();
        System.out.println("护理执行登记成功！记录号：" + record.getId());
    }

    private void listMyRecords() {
        ArrayList<CareRecord> list = dm.getRecords();
        if (list.isEmpty()) {
            System.out.println("无信息，请添加后查询");
            return;
        }

        System.out.println("记录号\t老人编号\t项目编码\t执行时间\t护工\t备注");
        boolean found = false;
        for (CareRecord r : list) {
            if (r.getNurseName() != null && r.getNurseName().equalsIgnoreCase(nurseName)) {
                found = true;
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                        r.getId(), r.getElderlyId(), r.getProjectCode(),
                        r.getExecuteTime(), r.getNurseName(),
                        r.getRemark() == null ? "" : r.getRemark());
            }
        }
        if (!found) {
            System.out.println("暂无本人执行的护理记录");
        }
    }

    private int readInt() {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("请输入数字：");
        }
        return sc.nextInt();
    }
}
