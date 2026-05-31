package neu.YYZX.gui;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import neu.YYZX.common.AuditLogger;
import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.model.*;
import neu.YYZX.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminFrame {

    private final User user;
    private final String token;
    private final DataInitializer ctx = DataInitializer.getInstance();
    private final UserService userService = new UserService(ctx.getUserDao());
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, ToggleButton> navButtons = new LinkedHashMap<>();
    private final Label moduleTitle = new Label();

    public AdminFrame(User user, String token) {
        this.user = user;
        this.token = token;
        this.stage = new Stage();
        stage.setTitle("东软颐养中心 - 管理员端 [" + user.getRealName() + "]");
        buildUI();
    }

    public void show() {
        stage.setScene(new Scene(root, 1100, 720));
        stage.setOnCloseRequest(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("退出确认");
            confirm.setHeaderText("确定要退出系统吗？");
            confirm.setContentText("未保存的数据将自动保存");
            confirm.showAndWait().ifPresent(r -> {
                if (r != ButtonType.OK) e.consume();
            });
        });
        stage.show();
    }

    private void buildUI() {
        // 左侧导航
        VBox nav = new VBox(3);
        nav.setPadding(new Insets(10));
        nav.setPrefWidth(160);
        nav.setStyle("-fx-background-color:#2c3e50");

        String[][] items = {
            {"📊 仪表盘", "dashboard"}, {"👤 用户管理", "users"}, {"🧓 老人管理", "elderly"},
            {"🛏 床位管理", "beds"}, {"⭐ 护理等级", "nlevels"}, {"📋 护理项目", "nprojects"},
            {"📝 护理记录", "nrecords"}, {"🍽 膳食管理", "foods"}, {"👷 员工管理", "employees"},
            {"🚶 外出手续", "outreg"}, {"🏠 退住管理", "checkout"}, {"💊 健康记录", "health"},
            {"📜 操作日志", "logs"}
        };

        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < items.length; i++) {
            ToggleButton btn = new ToggleButton(items[i][0]);
            btn.setToggleGroup(group);
            btn.setPrefWidth(140);
            btn.setAlignment(Pos.CENTER_LEFT);
            String key = items[i][1];
            navButtons.put(key, btn);
            btn.setOnAction(e -> switchContent(key));
            if (i == 0) btn.setSelected(true);
            nav.getChildren().add(btn);
        }
        updateNavStyles(null);

        // 标题栏
        moduleTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        moduleTitle.setTextFill(Color.web("#2c3e50"));
        moduleTitle.setPadding(new Insets(0, 0, 5, 0));

        // 内容区 = 标题 + 内容面板
        VBox contentWrapper = new VBox(10);
        contentWrapper.setPadding(new Insets(15, 20, 15, 20));
        contentWrapper.getChildren().addAll(moduleTitle, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // 顶部
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(8, 15, 8, 15));
        topBar.setStyle("-fx-background-color:#ecf0f1");
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Label userLabel = new Label("当前用户：" + user.getRealName() + "（" + user.getUsername() + "）");
        Button logoutBtn = new Button("退出登录");
        logoutBtn.setOnAction(e -> logout());
        topBar.getChildren().addAll(userLabel, logoutBtn);

        root.setTop(topBar);
        root.setLeft(nav);
        root.setCenter(contentWrapper);
        switchContent("dashboard");
    }

    private void updateNavStyles(String selectedKey) {
        for (Map.Entry<String, ToggleButton> entry : navButtons.entrySet()) {
            ToggleButton btn = entry.getValue();
            if (entry.getKey().equals(selectedKey)) {
                btn.setStyle("-fx-background-color:#3498db; -fx-text-fill:white; -fx-font-size:13px; "
                    + "-fx-background-radius:6; -fx-border-color:#2980b9; -fx-border-radius:6; -fx-border-width:2");
            } else {
                btn.setStyle("-fx-background-color:transparent; -fx-text-fill:#bdc3c7; -fx-font-size:13px; "
                    + "-fx-background-radius:6");
            }
        }
    }

    private Map<String, String> moduleNames = Map.ofEntries(
        Map.entry("dashboard", "仪表盘"), Map.entry("users", "用户管理"), Map.entry("elderly", "老人管理"),
        Map.entry("beds", "床位管理"), Map.entry("nlevels", "护理等级"), Map.entry("nprojects", "护理项目"),
        Map.entry("nrecords", "护理记录"), Map.entry("foods", "膳食管理"), Map.entry("employees", "员工管理"),
        Map.entry("outreg", "外出手续"), Map.entry("checkout", "退住管理"), Map.entry("health", "健康记录"),
        Map.entry("logs", "操作日志")
    );

    private void switchContent(String key) {
        updateNavStyles(key);
        moduleTitle.setText("▸ " + moduleNames.getOrDefault(key, key));

        javafx.scene.Node content;
        switch (key) {
            case "dashboard": content = buildDashboard(); break;
            case "users": content = buildUserManage(); break;
            case "elderly": content = buildElderlyManage(); break;
            case "beds": content = buildBedManage(); break;
            case "nlevels": content = buildNursingLevels(); break;
            case "nprojects": content = buildNursingProjects(); break;
            case "nrecords": content = buildNursingRecords(); break;
            case "foods": content = buildFoodManage(); break;
            case "employees": content = buildEmployeeManage(); break;
            case "outreg": content = buildOutRegManage(); break;
            case "checkout": content = buildCheckoutManage(); break;
            case "health": content = buildHealthRecords(); break;
            case "logs": content = buildLogView(); break;
            default: content = new Label("开发中...");
        }

        content.setOpacity(0);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);

        FadeTransition ft = new FadeTransition(Duration.millis(200), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void logout() {
        stage.close();
        // 重新显示登录
        Stage loginStage = new Stage();
        LoginPane loginPane = new LoginPane(loginStage);
        loginStage.setScene(new Scene(loginPane, 450, 420));
        loginStage.setTitle("东软颐养中心管理系统");
        loginStage.setResizable(false);
        loginStage.show();
    }

    // ==================== 仪表盘 ====================
    private VBox buildDashboard() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("系统仪表盘");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:bold");

        GridPane stats = new GridPane();
        stats.setHgap(30);
        stats.setVgap(15);
        stats.setAlignment(Pos.CENTER);

        int elderlyCount = ctx.getElderlyDao().size();
        int bedTotal = ctx.getBedDao().size();
        int bedUsed = 0;
        for (Bed b : ctx.getBedDao().findAll()) {
            if ("occupied".equals(b.getStatus())) bedUsed++;
        }
        int outCount = ctx.getOutRegistrationDao().size();
        int empCount = ctx.getEmployeeDao().size();
        int recordCount = ctx.getCareRecordDao().size();

        stats.add(statCard("在住老人", String.valueOf(elderlyCount)), 0, 0);
        stats.add(statCard("床位使用", bedUsed + "/" + bedTotal), 1, 0);
        stats.add(statCard("外出登记", String.valueOf(outCount)), 2, 0);
        stats.add(statCard("员工人数", String.valueOf(empCount)), 0, 1);
        stats.add(statCard("护理记录", String.valueOf(recordCount)), 1, 1);

        box.getChildren().addAll(title, stats);
        return box;
    }

    private VBox statCard(String label, String value) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:#ecf0f1;-fx-border-radius:8;-fx-background-radius:8");
        card.setPrefSize(200, 100);
        card.setAlignment(Pos.CENTER);
        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:#2c3e50");
        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size:14px;-fx-text-fill:#7f8c8d");
        card.getChildren().addAll(valLabel, nameLabel);
        return card;
    }

    // ==================== 用户管理 ====================
    private VBox buildUserManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<User> table = new TableView<>();
        TableColumn<User, String> c1 = tc("账号", "username");
        TableColumn<User, String> c2 = tc("角色", "role");
        TableColumn<User, String> c3 = tc("姓名", "realName");
        TableColumn<User, String> c4 = tc("电话", "phone");
        TableColumn<User, String> c5 = tc("创建时间", "createTime");
        table.getColumns().addAll(c1, c2, c3, c4, c5);
        refreshTable(table, ctx.getUserDao().findAll());

        Button addBtn = new Button("新增用户");
        Button delBtn = new Button("删除用户");
        Button chgPwdBtn = new Button("修改密码");

        addBtn.setOnAction(e -> {
            Dialog<User> dlg = userEditDialog(null);
            dlg.showAndWait().ifPresent(u -> {
                userService.addUser(u);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getUserDao().findAll());
            });
        });

        delBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择用户"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("删除确认");
            confirm.setHeaderText("确定要删除用户「" + sel.getUsername() + "」吗？");
            confirm.setContentText("此操作不可撤销");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    String err = userService.deleteUser(sel.getUsername());
                    if (err != null) { LoginPane.showAlert(Alert.AlertType.ERROR, err); return; }
                    PersistentIdGenerator.getInstance().save();
                    refreshTable(table, ctx.getUserDao().findAll());
                }
            });
        });

        chgPwdBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择用户"); return; }
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("修改密码");
            dlg.setHeaderText("为 " + sel.getUsername() + " 设置新密码");
            dlg.setContentText("新密码：");
            dlg.showAndWait().ifPresent(pwd -> {
                if (!pwd.isBlank()) {
                    userService.changePassword(sel.getUsername(), pwd);
                    PersistentIdGenerator.getInstance().save();
                    LoginPane.showAlert(Alert.AlertType.INFORMATION, "密码修改成功");
                }
            });
        });

        HBox btns = new HBox(10, addBtn, delBtn, chgPwdBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private Dialog<User> userEditDialog(User existing) {
        Dialog<User> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "新增用户" : "编辑用户");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField uname = new TextField();
        PasswordField pwd = new PasswordField();
        TextField rname = new TextField();
        TextField phone = new TextField();
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("admin", "nurse");
        roleBox.setValue("nurse");

        if (existing != null) {
            uname.setText(existing.getUsername());
            uname.setEditable(false);
            rname.setText(existing.getRealName());
            phone.setText(existing.getPhone());
            roleBox.setValue(existing.getRole());
        }

        grid.add(new Label("账号："), 0, 0); grid.add(uname, 1, 0);
        grid.add(new Label("密码："), 0, 1); grid.add(pwd, 1, 1);
        grid.add(new Label("姓名："), 0, 2); grid.add(rname, 1, 2);
        grid.add(new Label("电话："), 0, 3); grid.add(phone, 1, 3);
        grid.add(new Label("角色："), 0, 4); grid.add(roleBox, 1, 4);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || uname.getText().trim().isEmpty() || pwd.getText().isEmpty()) return null;
            String now = LocalDateTime.now().format(fmt);
            return new User(existing != null ? existing.getUserId() : null,
                uname.getText().trim(), pwd.getText(), roleBox.getValue(),
                rname.getText().trim(), phone.getText().trim(), null, null, now);
        });
        return dlg;
    }

    // ==================== 老人管理 ====================
    private VBox buildElderlyManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TextField searchField = new TextField();
        searchField.setPromptText("搜索老人姓名...");
        searchField.setPrefWidth(200);

        TableView<Elderly> table = elderlyTable();
        refreshElderly(table, ctx.getElderlyDao().findAll());

        searchField.textProperty().addListener((o, ov, nv) -> {
            if (nv.trim().isEmpty()) refreshElderly(table, ctx.getElderlyDao().findAll());
            else refreshElderly(table, ctx.getElderlyDao().findByName(nv.trim()));
        });

        Button checkinBtn = new Button("老人入住");
        Button setLevelBtn = new Button("设置护理等级");

        checkinBtn.setOnAction(e -> showCheckinDialog(table));
        setLevelBtn.setOnAction(e -> {
            Elderly sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择老人"); return; }
            showSetLevelDialog(sel);
            refreshElderly(table, ctx.getElderlyDao().findAll());
        });

        HBox btns = new HBox(10, searchField, checkinBtn, setLevelBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private void showCheckinDialog(TableView<Elderly> table) {
        Dialog<Elderly> dlg = new Dialog<>();
        dlg.setTitle("老人入住");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField name = new TextField();
        TextField age = new TextField();
        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("男", "女"); gender.setValue("男");
        TextField idCard = new TextField();
        TextField phone = new TextField();
        TextField address = new TextField();
        TextField emContact = new TextField();
        TextField emPhone = new TextField();
        ComboBox<String> bedBox = new ComboBox<>();
        ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus()))
            .forEach(b -> bedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
        DatePicker checkinDate = new DatePicker(LocalDate.now());

        grid.add(new Label("姓名："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("年龄："), 0, 1); grid.add(age, 1, 1);
        grid.add(new Label("性别："), 0, 2); grid.add(gender, 1, 2);
        grid.add(new Label("身份证："), 0, 3); grid.add(idCard, 1, 3);
        grid.add(new Label("电话："), 0, 4); grid.add(phone, 1, 4);
        grid.add(new Label("地址："), 0, 5); grid.add(address, 1, 5);
        grid.add(new Label("紧急联系人："), 0, 6); grid.add(emContact, 1, 6);
        grid.add(new Label("紧急电话："), 0, 7); grid.add(emPhone, 1, 7);
        grid.add(new Label("床位："), 0, 8); grid.add(bedBox, 1, 8);
        grid.add(new Label("入住日期："), 0, 9); grid.add(checkinDate, 1, 9);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("入住", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || name.getText().trim().isEmpty() || bedBox.getValue() == null) return null;
            String bedId = bedBox.getValue().split(" - ")[0];
            Bed bed = ctx.getBedDao().findById(bedId);
            if (bed != null) bed.setStatus("occupied");

            Elderly e = new Elderly();
            e.setName(name.getText().trim());
            try { e.setAge(Integer.parseInt(age.getText())); } catch (Exception ex) { e.setAge(0); }
            e.setGender(gender.getValue());
            e.setIdCard(idCard.getText().trim());
            e.setPhone(phone.getText().trim());
            e.setAddress(address.getText().trim());
            e.setEmergencyContact(emContact.getText().trim());
            e.setEmergencyPhone(emPhone.getText().trim());
            e.setBedId(bedId);
            e.setCheckInDate(checkinDate.getValue() != null ? checkinDate.getValue().toString() + " 00:00:00" : LocalDateTime.now().format(fmt));
            e.setStatus("在住");
            ctx.getElderlyDao().insert(e);
            PersistentIdGenerator.getInstance().save();
            refreshElderly(table, ctx.getElderlyDao().findAll());
            return e;
        });
        dlg.showAndWait();
    }

    private void showSetLevelDialog(Elderly e) {
        List<NursingLevel> levels = ctx.getNursingLevelDao().findAll();
        ChoiceDialog<String> dlg = new ChoiceDialog<>(
            e.getNursingLevelCode() != null ? e.getNursingLevelCode() : levels.get(0).getCode(),
            levels.stream().map(l -> l.getCode() + " - " + l.getName()).toList());
        dlg.setTitle("设置护理等级");
        dlg.setHeaderText("为 " + e.getName() + " 设置护理等级");
        dlg.showAndWait().ifPresent(sel -> {
            String code = sel.split(" - ")[0];
            e.setNursingLevelCode(code);
            ctx.getElderlyDao().update(e);
            PersistentIdGenerator.getInstance().save();
            AuditLogger.log("设置护理等级", "老人管理", e.getName() + " → " + code);
        });
    }

    // ==================== 床位管理 ====================
    private VBox buildBedManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        ComboBox<String> buildingBox = new ComboBox<>();
        ctx.getBuildingDao().findAll().forEach(b -> buildingBox.getItems().add(b.getBuildingId() + " " + b.getBuildingName()));
        buildingBox.setPromptText("选择楼栋");

        TableView<Bed> table = bedTable();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        buildingBox.setOnAction(e -> {
            String sel = buildingBox.getValue();
            if (sel != null) {
                String bid = sel.split(" ")[0];
                List<Room> rooms = ctx.getRoomDao().findByBuildingId(bid);
                List<Bed> beds = rooms.isEmpty() ? List.of() :
                    rooms.stream().flatMap(r -> ctx.getBedDao().findByRoomId(r.getRoomId()).stream()).toList();
                refreshBeds(table, beds);
            }
        });

        Button addBedBtn = new Button("添加床位");
        addBedBtn.setOnAction(e -> showAddBedDialog(table, buildingBox.getValue()));

        HBox btns = new HBox(10, buildingBox, addBedBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private void showAddBedDialog(TableView<Bed> table, String buildingInfo) {
        if (buildingInfo == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择楼栋"); return; }
        String buildingId = buildingInfo.split(" ")[0];
        List<Room> rooms = ctx.getRoomDao().findByBuildingId(buildingId);

        ChoiceDialog<String> dlg = new ChoiceDialog<>(
            rooms.get(0).getRoomId() + " - " + rooms.get(0).getRoomNo(),
            rooms.stream().map(r -> r.getRoomId() + " - " + r.getRoomNo() + " (" + r.getRoomType() + ")").toList());
        dlg.setTitle("选择房间");
        dlg.setHeaderText("请选择要添加床位的房间");

        dlg.showAndWait().ifPresent(sel -> {
            String roomId = sel.split(" - ")[0];
            TextInputDialog bedDlg = new TextInputDialog();
            bedDlg.setTitle("添加床位");
            bedDlg.setHeaderText("请输入床位号");
            bedDlg.showAndWait().ifPresent(bedNo -> {
                Bed bed = new Bed();
                bed.setRoomId(roomId);
                bed.setBedNo(bedNo.trim());
                bed.setStatus("available");
                ctx.getBedDao().insert(bed);
                PersistentIdGenerator.getInstance().save();
                // refresh
                List<Bed> beds = ctx.getBedDao().findByRoomId(roomId);
                refreshBeds(table, beds);
            });
        });
    }

    // ==================== 护理等级 ====================
    private VBox buildNursingLevels() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<NursingLevel> table = new TableView<>();
        TableColumn<NursingLevel, String> c1 = tc("代码", "code");
        TableColumn<NursingLevel, String> c2 = tc("名称", "name");
        TableColumn<NursingLevel, String> c3 = tc("描述", "description");
        TableColumn<NursingLevel, String> c4 = tc("频率", "frequency");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getNursingLevelDao().findAll());

        Button addBtn = new Button("新增护理等级");
        addBtn.setOnAction(e -> {
            Dialog<NursingLevel> dlg = nursingLevelDialog(null);
            dlg.showAndWait().ifPresent(l -> {
                ctx.getNursingLevelDao().insert(l);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getNursingLevelDao().findAll());
            });
        });

        box.getChildren().addAll(addBtn, table);
        return box;
    }

    private Dialog<NursingLevel> nursingLevelDialog(NursingLevel existing) {
        Dialog<NursingLevel> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "新增护理等级" : "编辑护理等级");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField code = new TextField();
        TextField name = new TextField();
        TextField desc = new TextField();
        TextField freq = new TextField();
        if (existing != null) {
            code.setText(existing.getCode()); name.setText(existing.getName());
            desc.setText(existing.getDescription()); freq.setText(existing.getFrequency());
        }
        grid.add(new Label("代码："), 0, 0); grid.add(code, 1, 0);
        grid.add(new Label("名称："), 0, 1); grid.add(name, 1, 1);
        grid.add(new Label("描述："), 0, 2); grid.add(desc, 1, 2);
        grid.add(new Label("频率："), 0, 3); grid.add(freq, 1, 3);
        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn != okBtn || code.getText().trim().isEmpty()) return null;
            return new NursingLevel(code.getText().trim(), name.getText().trim(), desc.getText().trim(), freq.getText().trim());
        });
        return dlg;
    }

    // ==================== 护理项目 ====================
    private VBox buildNursingProjects() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CareProject> table = new TableView<>();
        TableColumn<CareProject, String> c1 = tc("代码", "code");
        TableColumn<CareProject, String> c2 = tc("名称", "name");
        TableColumn<CareProject, String> c3 = tc("类别", "category");
        TableColumn<CareProject, String> c4 = tc("价格", "price");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getCareProjectDao().findAll());

        Button addBtn = new Button("新增护理项目");
        addBtn.setOnAction(e -> {
            Dialog<CareProject> dlg = careProjectDialog(null);
            dlg.showAndWait().ifPresent(p -> {
                ctx.getCareProjectDao().insert(p);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getCareProjectDao().findAll());
            });
        });

        box.getChildren().addAll(addBtn, table);
        return box;
    }

    private Dialog<CareProject> careProjectDialog(CareProject existing) {
        Dialog<CareProject> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "新增护理项目" : "编辑护理项目");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField code = new TextField(), name = new TextField(), cat = new TextField(),
                   unit = new TextField(), price = new TextField(), cycle = new TextField(), remark = new TextField();
        grid.add(new Label("代码："), 0, 0); grid.add(code, 1, 0);
        grid.add(new Label("名称："), 0, 1); grid.add(name, 1, 1);
        grid.add(new Label("类别："), 0, 2); grid.add(cat, 1, 2);
        grid.add(new Label("单位："), 0, 3); grid.add(unit, 1, 3);
        grid.add(new Label("价格："), 0, 4); grid.add(price, 1, 4);
        grid.add(new Label("周期："), 0, 5); grid.add(cycle, 1, 5);
        grid.add(new Label("备注："), 0, 6); grid.add(remark, 1, 6);
        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn != okBtn || code.getText().trim().isEmpty()) return null;
            return new CareProject(code.getText().trim(), name.getText().trim(), cat.getText().trim(),
                unit.getText().trim(), Double.parseDouble(price.getText().isEmpty() ? "0" : price.getText()),
                cycle.getText().trim(), remark.getText().trim());
        });
        return dlg;
    }

    // ==================== 护理记录 ====================
    private VBox buildNursingRecords() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CareRecord> table = new TableView<>();
        TableColumn<CareRecord, String> c1 = tc("老人ID", "elderlyId");
        TableColumn<CareRecord, String> c2 = tc("项目代码", "projectCode");
        TableColumn<CareRecord, String> c3 = tc("执行时间", "executeTime");
        TableColumn<CareRecord, String> c4 = tc("护工", "nurseName");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getCareRecordDao().findAll());

        box.getChildren().add(table);
        return box;
    }

    // ==================== 膳食管理 ====================
    private VBox buildFoodManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<Food> table = new TableView<>();
        TableColumn<Food, String> c1 = tc("名称", "foodName");
        TableColumn<Food, String> c2 = tc("类别", "category");
        TableColumn<Food, String> c3 = tc("单位", "unit");
        TableColumn<Food, String> c4 = tc("价格", "price");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getFoodDao().findAll());

        Button addBtn = new Button("新增食物");
        addBtn.setOnAction(e -> {
            Dialog<Food> dlg = foodDialog(null);
            dlg.showAndWait().ifPresent(f -> {
                ctx.getFoodDao().insert(f);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getFoodDao().findAll());
            });
        });
        Button delBtn = new Button("删除食物");
        delBtn.setOnAction(e -> {
            Food sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择食物"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("删除确认");
            confirm.setHeaderText("确定要删除食物「" + sel.getFoodName() + "」吗？");
            confirm.setContentText("此操作不可撤销");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    ctx.getFoodDao().delete(sel.getFoodId());
                    PersistentIdGenerator.getInstance().save();
                    refreshTable(table, ctx.getFoodDao().findAll());
                }
            });
        });

        HBox btns = new HBox(10, addBtn, delBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private Dialog<Food> foodDialog(Food existing) {
        Dialog<Food> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "新增食物" : "编辑食物");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField name = new TextField(), cat = new TextField(), unit = new TextField(),
                   price = new TextField(), nutrition = new TextField(), remark = new TextField();
        grid.add(new Label("名称："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("类别："), 0, 1); grid.add(cat, 1, 1);
        grid.add(new Label("单位："), 0, 2); grid.add(unit, 1, 2);
        grid.add(new Label("价格："), 0, 3); grid.add(price, 1, 3);
        grid.add(new Label("营养："), 0, 4); grid.add(nutrition, 1, 4);
        grid.add(new Label("备注："), 0, 5); grid.add(remark, 1, 5);
        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn != okBtn || name.getText().trim().isEmpty()) return null;
            return new Food(null, name.getText().trim(), cat.getText().trim(), unit.getText().trim(),
                Double.parseDouble(price.getText().isEmpty() ? "0" : price.getText()),
                nutrition.getText().trim(), remark.getText().trim());
        });
        return dlg;
    }

    // ==================== 员工管理 ====================
    private VBox buildEmployeeManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<Employee> table = new TableView<>();
        TableColumn<Employee, String> c1 = tc("姓名", "name");
        TableColumn<Employee, String> c2 = tc("性别", "gender");
        TableColumn<Employee, String> c3 = tc("职位", "position");
        TableColumn<Employee, String> c4 = tc("电话", "phone");
        TableColumn<Employee, String> c5 = tc("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5);
        refreshTable(table, ctx.getEmployeeDao().findAll());

        Button addBtn = new Button("新增员工");
        addBtn.setOnAction(e -> {
            Dialog<Employee> dlg = employeeDialog(null);
            dlg.showAndWait().ifPresent(emp -> {
                ctx.getEmployeeDao().insert(emp);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getEmployeeDao().findAll());
            });
        });
        Button delBtn = new Button("删除员工");
        delBtn.setOnAction(e -> {
            Employee sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择员工"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("删除确认");
            confirm.setHeaderText("确定要删除员工「" + sel.getName() + "」吗？");
            confirm.setContentText("此操作不可撤销");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    ctx.getEmployeeDao().delete(sel.getEmployeeId());
                    PersistentIdGenerator.getInstance().save();
                    refreshTable(table, ctx.getEmployeeDao().findAll());
                }
            });
        });

        HBox btns = new HBox(10, addBtn, delBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private Dialog<Employee> employeeDialog(Employee existing) {
        Dialog<Employee> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "新增员工" : "编辑员工");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        TextField name = new TextField(), gender = new TextField("男"), position = new TextField(),
                   phone = new TextField(), idCard = new TextField(), salary = new TextField();
        DatePicker entryDate = new DatePicker(LocalDate.now());
        grid.add(new Label("姓名："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("性别："), 0, 1); grid.add(gender, 1, 1);
        grid.add(new Label("职位："), 0, 2); grid.add(position, 1, 2);
        grid.add(new Label("电话："), 0, 3); grid.add(phone, 1, 3);
        grid.add(new Label("身份证："), 0, 4); grid.add(idCard, 1, 4);
        grid.add(new Label("工资："), 0, 5); grid.add(salary, 1, 5);
        grid.add(new Label("入职日期："), 0, 6); grid.add(entryDate, 1, 6);
        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn != okBtn || name.getText().trim().isEmpty()) return null;
            String entryDateStr = entryDate.getValue() != null ? entryDate.getValue().toString() + " 00:00:00" : LocalDateTime.now().format(fmt);
            return new Employee(null, name.getText().trim(), gender.getText().trim(), position.getText().trim(),
                phone.getText().trim(), idCard.getText().trim(),
                entryDateStr,
                Double.parseDouble(salary.getText().isEmpty() ? "0" : salary.getText()),
                "在职", "");
        });
        return dlg;
    }

    // ==================== 外出手续 ====================
    private VBox buildOutRegManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        Label title = new Label("外出登记列表（双击标记已归/超时）");
        TableView<OutRegistration> table = new TableView<>();
        TableColumn<OutRegistration, String> c1 = tc("老人ID", "customerId");
        TableColumn<OutRegistration, String> c2 = tc("外出时间", "outTime");
        TableColumn<OutRegistration, String> c3 = tc("预计归时", "expectedReturnTime");
        TableColumn<OutRegistration, String> c4 = tc("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getOutRegistrationDao().findAll());
        box.getChildren().addAll(title, table);
        return box;
    }

    // ==================== 退住管理 ====================
    private VBox buildCheckoutManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        Label title = new Label("退住登记列表");
        TableView<CheckOut> table = new TableView<>();
        TableColumn<CheckOut, String> c1 = tc("老人ID", "customerId");
        TableColumn<CheckOut, String> c2 = tc("退住日期", "checkoutDate");
        TableColumn<CheckOut, String> c3 = tc("原因", "reason");
        table.getColumns().addAll(c1, c2, c3);
        refreshTable(table, ctx.getCheckOutDao().findAll());
        box.getChildren().addAll(title, table);
        return box;
    }

    // ==================== 健康记录 ====================
    private VBox buildHealthRecords() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        TableView<HealthRecord> table = new TableView<>();
        TableColumn<HealthRecord, String> c1 = tc("老人ID", "customerId");
        TableColumn<HealthRecord, String> c2 = tc("记录日期", "recordDate");
        TableColumn<HealthRecord, String> c3 = tc("血压", "bloodPressure");
        TableColumn<HealthRecord, String> c4 = tc("心率", "heartRate");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getHealthRecordDao().findAll());
        box.getChildren().add(table);
        return box;
    }

    // ==================== 操作日志 ====================
    private VBox buildLogView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        TableView<OperationLog> table = new TableView<>();
        TableColumn<OperationLog, String> c1 = tc("操作人", "operatorName");
        TableColumn<OperationLog, String> c2 = tc("操作", "action");
        TableColumn<OperationLog, String> c3 = tc("目标", "target");
        TableColumn<OperationLog, String> c4 = tc("时间", "time");
        table.getColumns().addAll(c1, c2, c3, c4);
        refreshTable(table, ctx.getOperationLogDao().findAll());
        box.getChildren().add(table);
        return box;
    }

    // ==================== 辅助方法 ====================
    private <T> TableColumn<T, String> tc(String title, String property) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(120);
        return col;
    }

    private <T> void refreshTable(TableView<T> table, List<T> data) {
        table.setItems(FXCollections.observableArrayList(data));
    }

    private void refreshElderly(TableView<Elderly> table, List<Elderly> data) {
        table.setItems(FXCollections.observableArrayList(data));
    }

    private void refreshBeds(TableView<Bed> table, List<Bed> data) {
        table.setItems(FXCollections.observableArrayList(data));
    }

    private TableView<Elderly> elderlyTable() {
        TableView<Elderly> table = new TableView<>();
        TableColumn<Elderly, String> c1 = tc("姓名", "name");
        TableColumn<Elderly, String> c2 = tc("年龄", "age");
        TableColumn<Elderly, String> c3 = tc("性别", "gender");
        TableColumn<Elderly, String> c4 = tc("床位ID", "bedId");
        TableColumn<Elderly, String> c5 = tc("护理等级", "nursingLevelCode");
        TableColumn<Elderly, String> c6 = tc("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);
        return table;
    }

    private TableView<Bed> bedTable() {
        TableView<Bed> table = new TableView<>();
        TableColumn<Bed, String> c1 = tc("床位号", "bedNo");
        TableColumn<Bed, String> c2 = tc("房间ID", "roomId");
        TableColumn<Bed, String> c3 = tc("状态", "status");
        table.getColumns().addAll(c1, c2, c3);
        return table;
    }
}
