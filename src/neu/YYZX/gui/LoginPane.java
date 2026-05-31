package neu.YYZX.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import neu.YYZX.common.AuditLogger;
import neu.YYZX.common.LoginContext;
import neu.YYZX.model.User;
import neu.YYZX.service.DataInitializer;
import neu.YYZX.service.UserService;

public class LoginPane extends VBox {

    private final Stage stage;
    private final UserService userService;
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final ComboBox<String> roleBox = new ComboBox<>();

    public LoginPane(Stage stage) {
        this.stage = stage;
        this.userService = new UserService(DataInitializer.getInstance().getUserDao());

        setAlignment(Pos.CENTER);
        setSpacing(12);
        setPadding(new Insets(30));

        Label title = new Label("东软颐养中心管理系统");
        title.setStyle("-fx-font-size:20px;-fx-font-weight:bold");

        roleBox.getItems().addAll("admin", "nurse");
        roleBox.setValue("admin");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("账号："), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("密码："), 0, 1);
        form.add(passwordField, 1, 1);
        form.add(new Label("角色："), 0, 2);
        form.add(roleBox, 1, 2);

        Button loginBtn = new Button("登 录");
        loginBtn.setPrefWidth(200);
        loginBtn.setOnAction(e -> login());

        HBox links = new HBox(10);
        links.setAlignment(Pos.CENTER);
        Hyperlink registerLink = new Hyperlink("注册账号");
        registerLink.setOnAction(e -> showRegisterDialog());
        Hyperlink forgotLink = new Hyperlink("找回密码");
        forgotLink.setOnAction(e -> showForgotDialog());
        links.getChildren().addAll(registerLink, new Label("|"), forgotLink);

        getChildren().addAll(title, form, loginBtn, links);

        passwordField.setOnAction(e -> login());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "请输入账号和密码");
            return;
        }

        User user = userService.authenticate(username, password, role);
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "账号、密码或角色不正确");
            return;
        }

        String token = LoginContext.login(user);
        AuditLogger.setCurrentUser(user);
        stage.hide();

        if ("admin".equals(user.getRole())) {
            new AdminFrame(user, token).show();
        } else {
            new NurseFrame(user, token).show();
        }
    }

    private void showRegisterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("账号注册");
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField uname = new TextField();
        PasswordField pwd = new PasswordField();
        PasswordField confirmPwd = new PasswordField();
        TextField rname = new TextField();
        TextField phone = new TextField();
        ComboBox<String> questionBox = new ComboBox<>();
        questionBox.getItems().addAll(
            "你的母亲的名字是什么？",
            "你最喜欢的城市是哪里？",
            "你的小学名称是什么？",
            "你的第一只宠物叫什么名字？",
            "你最喜欢的电影是什么？"
        );
        questionBox.setValue("你的母亲的名字是什么？");
        TextField answer = new TextField();

        grid.add(new Label("账号："), 0, 0); grid.add(uname, 1, 0);
        grid.add(new Label("密码："), 0, 1); grid.add(pwd, 1, 1);
        grid.add(new Label("确认密码："), 0, 2); grid.add(confirmPwd, 1, 2);
        grid.add(new Label("姓名："), 0, 3); grid.add(rname, 1, 3);
        grid.add(new Label("电话："), 0, 4); grid.add(phone, 1, 4);
        grid.add(new Label("密保问题："), 0, 5); grid.add(questionBox, 1, 5);
        grid.add(new Label("密保答案："), 0, 6); grid.add(answer, 1, 6);

        dialog.getDialogPane().setContent(grid);

        ButtonType registerBtn = new ButtonType("注册", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == registerBtn) {
                if (uname.getText().trim().isEmpty() || pwd.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "账号和密码不能为空");
                    return null;
                }
                if (!pwd.getText().equals(confirmPwd.getText())) {
                    showAlert(Alert.AlertType.WARNING, "两次密码输入不一致");
                    return null;
                }
                if (questionBox.getValue() == null || answer.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "请设置密保问题和答案");
                    return null;
                }
                if (userService.containsUsername(uname.getText().trim())) {
                    showAlert(Alert.AlertType.ERROR, "账号已存在");
                    return null;
                }
                userService.addUser(new User(null, uname.getText().trim(), pwd.getText(),
                    "nurse", rname.getText().trim(), phone.getText().trim(),
                    questionBox.getValue(), answer.getText().trim(),
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                showAlert(Alert.AlertType.INFORMATION, "注册成功，请登录");
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showForgotDialog() {
        TextInputDialog qDialog = new TextInputDialog();
        qDialog.setTitle("找回密码");
        qDialog.setHeaderText("请输入账号");
        qDialog.setContentText("账号：");

        String username = qDialog.showAndWait().orElse("");
        if (username.trim().isEmpty()) return;

        User user = userService.findByUsername(username.trim());
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "账号不存在");
            return;
        }
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "该账号未设置密保问题");
            return;
        }

        TextInputDialog aDialog = new TextInputDialog();
        aDialog.setTitle("找回密码");
        aDialog.setHeaderText("密保问题：" + user.getSecurityQuestion());
        aDialog.setContentText("答案：");

        String answer = aDialog.showAndWait().orElse("");
        if (answer.trim().isEmpty()) return;

        if (!answer.trim().equals(user.getSecurityAnswer())) {
            showAlert(Alert.AlertType.ERROR, "密保答案错误");
            return;
        }

        Dialog<String> pwdDialog = new Dialog<>();
        pwdDialog.setTitle("重置密码");
        pwdDialog.setHeaderText("请输入新密码");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        PasswordField newPwd = new PasswordField();
        PasswordField confirmPwd = new PasswordField();
        grid.add(new Label("新密码："), 0, 0); grid.add(newPwd, 1, 0);
        grid.add(new Label("确认密码："), 0, 1); grid.add(confirmPwd, 1, 1);

        pwdDialog.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("重置", ButtonBar.ButtonData.OK_DONE);
        pwdDialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        pwdDialog.setResultConverter(btn -> {
            if (btn == okBtn) {
                if (newPwd.getText().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "密码不能为空");
                    return null;
                }
                if (!newPwd.getText().equals(confirmPwd.getText())) {
                    showAlert(Alert.AlertType.WARNING, "两次密码输入不一致");
                    return null;
                }
                if (newPwd.getText().equals(user.getPassword())) {
                    showAlert(Alert.AlertType.ERROR, "新密码不能与原密码相同");
                    return null;
                }
                return newPwd.getText();
            }
            return null;
        });

        String newPassword = pwdDialog.showAndWait().orElse(null);
        if (newPassword != null) {
            userService.changePassword(username.trim(), newPassword);
            showAlert(Alert.AlertType.INFORMATION, "密码重置成功，请登录");
        }
    }

    static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }
}
