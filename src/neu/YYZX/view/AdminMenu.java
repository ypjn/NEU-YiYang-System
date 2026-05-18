package neu.YYZX.view;

import java.util.Scanner;

public class AdminMenu {
    public void execute(){
        //管理员界面
        System.out.println("======系统管理员登录======");
        System.out.println("请输入账号：");
        Scanner sc=new Scanner(System.in);
        String userName=sc.next();
        System.out.println("请输入密码：");
        String password=sc.next();
        //登录合法信息校验
        if(userName.equalsIgnoreCase("admin")&&password.equalsIgnoreCase("admin")){
            System.out.println("登录成功！");
        }else{
            System.out.println("登录失败！");
        }
        System.out.println("=========================");
    }
}
