package neu.YYZX.view;

import neu.YYZX.common.IMenu;
import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.service.DataInitializer;

import java.util.Scanner;

/**
 * 系统主菜单（程序入口）
 */
public class mainMenu implements IMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        DataInitializer.getInstance().init();
        new mainMenu().show();
    }

    @Override
    public void show() {
        while (true) {
            System.out.println();
            System.out.println("==========东软颐养中心管理系统==========");
            System.out.println("欢迎来到东软颐养中心管理系统");
            System.out.println("1---------管理员登录");
            System.out.println("2---------护工登录");
            System.out.println("3---------退出系统");
            System.out.print("请选择：");

            int choice = readInt();
            switch (choice) {
                case 1:
                    new AdminMenu().show();
                    break;
                case 2:
                    new NurseMenu().show();
                    break;
                case 3:
                    PersistentIdGenerator.getInstance().save();
                    System.out.println("数据已保存，感谢使用！");
                    System.exit(0);
                    break;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }

    public static int readInt() {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("请输入数字：");
        }
        int value = sc.nextInt();
        sc.nextLine(); // 清除换行符
        return value;
    }

    public static double readDouble() {
        while (!sc.hasNextDouble()) {
            sc.next();
            System.out.print("请输入数字：");
        }
        double value = sc.nextDouble();
        sc.nextLine(); // 清除换行符
        return value;
    }

    public static String readLine() {
        return sc.nextLine();
    }

    /** 读取一个单词并清除换行符 */
    public static String readWord() {
        String value = sc.next();
        sc.nextLine();
        return value;
    }

    public static Scanner getScanner() {
        return sc;
    }
}
