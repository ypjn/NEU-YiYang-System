package neu.YYZX;

import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.service.DataInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YYZXApplication {

    public static void main(String[] args) {
        DataInitializer.getInstance().init();
        SpringApplication.run(YYZXApplication.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PersistentIdGenerator.getInstance().save();
            System.out.println("数据已保存");
        }));
    }
}
