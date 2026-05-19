package neu.YYZX.view;

import neu.YYZX.service.DataManager;
import neu.YYZX.util.FileUtil;

import java.util.Scanner;

public class mainMenu {
    public static void main(String[] args) {
        DataManager dm = DataManager.getInstance();
        FileUtil.load(dm);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("==========东软颐养中心管理系统==========");
            System.out.println("欢迎来到东软颐养中心管理系统");
            System.out.println("1---------管理员登录");
            System.out.println("2---------护工登录");
            System.out.println("3---------退出");
            System.out.print("请选择：");

            if (!sc.hasNextInt()) {
                sc.next();
                System.out.println("输入有误，请重新输入");
                continue;
            }

            int result = sc.nextInt();
            switch (result) {
                case 1:
                    new AdminMenu(sc).execute();
                    break;
                case 2:
                    new NurseMenu(sc).execute();
                    break;
                case 3:
                    FileUtil.persist();
                    System.out.println("退出系统，感谢使用！");
                    System.exit(0);
                    break;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }
}
