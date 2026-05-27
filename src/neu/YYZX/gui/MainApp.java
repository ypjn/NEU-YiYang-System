package neu.YYZX.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import neu.YYZX.service.DataInitializer;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        DataInitializer.getInstance().init();

        LoginPane loginPane = new LoginPane(stage);
        Scene scene = new Scene(loginPane, 450, 420);
        stage.setScene(scene);
        stage.setTitle("东软颐养中心管理系统");
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        DataInitializer.getInstance().saveAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
