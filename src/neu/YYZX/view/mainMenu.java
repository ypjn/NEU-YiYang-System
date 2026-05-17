package neu.YYZX.view;

import java.util.Scanner;

public class mainMenu {
    public static void main(String[] args) {
        while (true) {
            System.out.println("欢迎来到东软颐养中心管理系统");
            System.out.println("1---------管理员登录");
            System.out.println("2---------护工登录");
            System.out.println("3---------退出");

            System.out.println("==========东软颐养中心管理系统=========");
            System.out.println("1---------管理员登录");
            System.out.println("2---------护工登录");
            System.out.println("3---------退出");

            Scanner sc = new Scanner(System.in);

            int result = sc.nextInt();
            System.out.println("用户输入的是：" + result);

            switch (result) {
                case 1:
                    System.out.println("即将进行管理员登录");
                    break;
                case 2:
                    System.out.println("即将进行护工登录");
                    break;
                case 3:
                    System.out.println("退出系统");
                    System.exit(1);
                    break;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }
}
