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
        AuditLogger.setCurrentUser(user);
        this.stage = new Stage();
        stage.setTitle("东软颐养中心 - 管理员端 [" + user.getRealName() + "]");
        buildUI();
    }

    public void show() {
        stage.setScene(new Scene(root, 1100, 720));
        stage.setOnCloseRequest(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.initOwner(stage);
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
            {"仪表盘", "dashboard"}, {"用户管理", "users"}, {"老人管理", "elderly"},
            {"床位管理", "beds"}, {"护理等级", "nlevels"}, {"护理项目", "nprojects"},
            {"护理记录", "nrecords"}, {"服务关注", "serviceFocus"}, {"管家分配", "butlerAssign"}, {"膳食管理", "foods"}, {"员工管理", "employees"},
            {"外出手续", "outreg"}, {"退住管理", "checkout"}, {"健康记录", "health"},
            {"操作日志", "logs"}
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
                btn.setStyle("-fx-background-color:#3498db; -fx-text-fill:#1a1a2e; -fx-font-size:13px; "
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
        Map.entry("nrecords", "护理记录"), Map.entry("serviceFocus", "服务关注"), Map.entry("butlerAssign", "管家分配"), Map.entry("foods", "膳食管理"), Map.entry("employees", "员工管理"),
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
            case "serviceFocus": content = buildServiceFocus(); break;
            case "butlerAssign": content = buildButlerAssign(); break;
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

        stats.add(statCard("在住老人", String.valueOf(elderlyCount), "elderly"), 0, 0);
        stats.add(statCard("床位使用", bedUsed + "/" + bedTotal, "beds"), 1, 0);
        stats.add(statCard("外出登记", String.valueOf(outCount), "outreg"), 2, 0);
        stats.add(statCard("员工人数", String.valueOf(empCount), "employees"), 0, 1);
        stats.add(statCard("护理记录", String.valueOf(recordCount), "nrecords"), 1, 1);

        box.getChildren().addAll(title, stats);
        return box;
    }

    private VBox statCard(String label, String value, String targetModule) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:#ecf0f1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand");
        card.setPrefSize(200, 100);
        card.setAlignment(Pos.CENTER);
        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:#2c3e50");
        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size:14px;-fx-text-fill:#7f8c8d");
        card.getChildren().addAll(valLabel, nameLabel);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#d5e8f0;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ecf0f1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand"));
        card.setOnMouseClicked(e -> switchContent(targetModule));
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
                boolean success = userService.addUser(u);
                if (!success) {
                    LoginPane.showAlert(Alert.AlertType.ERROR, "添加失败：账号「" + u.getUsername() + "」已存在，请使用其他账号名");
                    return;
                }
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getUserDao().findAll());
                LoginPane.showAlert(Alert.AlertType.INFORMATION, "用户添加成功");
            });
        });


        delBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择用户"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.initOwner(stage);
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
        dlg.initOwner(stage);
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
        searchField.setPromptText("搜索姓名/身份证/电话...");
        searchField.setPrefWidth(200);

        ComboBox<String> levelFilter = new ComboBox<>();
        levelFilter.setPrefWidth(120);
        levelFilter.getItems().add("全部等级");
        ctx.getNursingLevelDao().findAll().forEach(l -> levelFilter.getItems().add(l.getCode() + " " + l.getName()));
        levelFilter.setValue("全部等级");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.setPrefWidth(100);
        statusFilter.getItems().addAll("全部状态", "在住", "外出", "已退住");
        statusFilter.setValue("全部状态");

        TableView<Elderly> table = elderlyTable();

        Runnable doFilter = () -> {
            String kw = searchField.getText().trim();
            String lv = levelFilter.getValue();
            String st = statusFilter.getValue();
            List<Elderly> all = ctx.getElderlyDao().findAll();
            List<Elderly> filtered = all.stream().filter(e -> {
                // 姓名/身份证/电话 模糊搜索
                if (!kw.isEmpty()) {
                    boolean matchName = e.getName() != null && e.getName().contains(kw);
                    boolean matchIdCard = e.getIdCard() != null && e.getIdCard().contains(kw);
                    boolean matchPhone = e.getPhone() != null && e.getPhone().contains(kw);
                    if (!matchName && !matchIdCard && !matchPhone) return false;
                }
                // 护理等级筛选
                if (!"全部等级".equals(lv)) {
                    String code = lv.split(" ")[0];
                    if (e.getNursingLevelCode() == null || !e.getNursingLevelCode().equals(code)) return false;
                }
                // 状态筛选
                if (!"全部状态".equals(st)) {
                    if (e.getStatus() == null || !e.getStatus().equals(st)) return false;
                }
                return true;
            }).toList();
            refreshElderly(table, filtered);
        };

        searchField.textProperty().addListener((o, ov, nv) -> doFilter.run());
        levelFilter.setOnAction(e -> doFilter.run());
        statusFilter.setOnAction(e -> doFilter.run());
        refreshElderly(table, ctx.getElderlyDao().findAll());

        table.setRowFactory(tv -> {
            TableRow<Elderly> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showElderlyInfoDialog(row.getItem());
                }
            });
            return row;
        });

        Button checkinBtn = new Button("老人入住");
        Button editBtn = new Button("编辑信息");
        Button setLevelBtn = new Button("设置护理等级");

        checkinBtn.setOnAction(e -> {
            long availBeds = ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus())).count();
            if (availBeds == 0) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "没有空闲床位，请先在床位管理中创建房间和床位");
                return;
            }
            showCheckinDialog(table);
        });
        editBtn.setOnAction(e -> {
            Elderly sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要编辑的老人"); return; }
            showEditElderlyDialog(sel, table);
        });
        setLevelBtn.setOnAction(e -> {
            Elderly sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择老人"); return; }
            showSetLevelDialog(sel);
            doFilter.run();
        });

        HBox btns = new HBox(10, searchField, levelFilter, statusFilter, checkinBtn, editBtn, setLevelBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private void showCheckinDialog(TableView<Elderly> table) {
        Dialog<Elderly> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("老人入住");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField name = new TextField();
        TextField age = new TextField();
        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("男", "女"); gender.setValue("男");
        TextField idCard = new TextField();
        ComboBox<String> bloodType = new ComboBox<>();
        bloodType.getItems().addAll("A", "B", "AB", "O"); bloodType.setValue("A");
        DatePicker birthDate = new DatePicker();
        TextField phone = new TextField();
        TextField address = new TextField();
        TextField familyMember = new TextField();
        TextField emContact = new TextField();
        TextField emPhone = new TextField();
        ComboBox<String> bedBox = new ComboBox<>();
        ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus()))
            .forEach(b -> bedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
        if (!bedBox.getItems().isEmpty()) bedBox.setValue(bedBox.getItems().get(0));
        DatePicker checkinDate = new DatePicker(LocalDate.now());
        DatePicker contractEndDate = new DatePicker(LocalDate.now().plusYears(1));

        // 身份证校验 + 自动提取生日/性别
        Label idCardMsg = new Label();
        idCardMsg.setStyle("-fx-font-size:11px");
        idCard.textProperty().addListener((o, ov, nv) -> {
            String result = validateIdCard(nv);
            if (result == null) {
                idCard.setStyle("-fx-border-color:red; -fx-border-width:1px");
                idCardMsg.setText(nv.length() == 18 ? "身份证格式错误" : "");
                idCardMsg.setStyle("-fx-text-fill:red; -fx-font-size:11px");
            } else {
                idCard.setStyle("");
                // 自动提取并填充出生日期
                String birth = nv.substring(6, 14);
                LocalDate bd = LocalDate.parse(birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8));
                birthDate.setValue(bd);
                // 自动计算年龄
                int calculatedAge = LocalDate.now().getYear() - bd.getYear();
                age.setText(String.valueOf(calculatedAge));
                // 自动识别性别 (第17位，奇数为男)
                int seqDigit = Integer.parseInt(nv.substring(14, 17));
                gender.setValue(seqDigit % 2 == 1 ? "男" : "女");
                idCardMsg.setText("✓ 出生: " + result + "  性别: " + gender.getValue() + "  年龄: " + calculatedAge);
                idCardMsg.setStyle("-fx-text-fill:green; -fx-font-size:11px");
            }
        });

        grid.add(new Label("姓名："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("年龄："), 0, 1); grid.add(age, 1, 1);
        grid.add(new Label("出生日期："), 2, 1); grid.add(birthDate, 3, 1);
        grid.add(new Label("性别："), 0, 2); grid.add(gender, 1, 2);
        grid.add(new Label("血型："), 2, 2); grid.add(bloodType, 3, 2);
        grid.add(new Label("身份证："), 0, 3); grid.add(idCard, 1, 3);
        grid.add(idCardMsg, 2, 3, 2, 1);
        grid.add(new Label("电话："), 0, 4); grid.add(phone, 1, 4);
        grid.add(new Label("地址："), 2, 4); grid.add(address, 3, 4);
        grid.add(new Label("家属："), 0, 5); grid.add(familyMember, 1, 5);
        grid.add(new Label("紧急联系人："), 2, 5); grid.add(emContact, 3, 5);
        grid.add(new Label("紧急电话："), 0, 6); grid.add(emPhone, 1, 6);
        grid.add(new Label("床位："), 2, 6); grid.add(bedBox, 3, 6);
        grid.add(new Label("入住日期："), 0, 7); grid.add(checkinDate, 1, 7);
        grid.add(new Label("合同到期："), 2, 7); grid.add(contractEndDate, 3, 7);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("入住", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || name.getText().trim().isEmpty() || bedBox.getValue() == null) return null;
            String idCardText = idCard.getText().trim();
            if (!idCardText.isEmpty() && validateIdCard(idCardText) == null) {
                // 表单上已有红色提示，不弹窗，保持对话框打开
                return null;
            }

            String bedId = bedBox.getValue().split(" - ")[0];
            Bed bed = ctx.getBedDao().findById(bedId);
            if (bed != null) {
                bed.setStatus("occupied");
                ctx.getBedDao().update(bed);
            }

            Elderly e = new Elderly();
            e.setName(name.getText().trim());
            e.setGender(gender.getValue());
            e.setIdCard(idCardText);
            e.setBloodType(bloodType.getValue());
            if (birthDate.getValue() != null) {
                e.setBirthDate(birthDate.getValue().toString());
                e.setAge(LocalDate.now().getYear() - birthDate.getValue().getYear());
            } else {
                try { e.setAge(Integer.parseInt(age.getText())); } catch (Exception ex) { e.setAge(0); }
            }
            e.setPhone(phone.getText().trim());
            e.setAddress(address.getText().trim());
            e.setFamilyMember(familyMember.getText().trim());
            e.setEmergencyContact(emContact.getText().trim());
            e.setEmergencyPhone(emPhone.getText().trim());
            e.setBedId(bedId);
            if (bed != null) {
                Room room = ctx.getRoomDao().findById(bed.getRoomId());
                if (room != null) {
                    e.setRoomNo(room.getRoomNo());
                    e.setBuildingId(room.getBuildingId());
                }
            }
            e.setCheckInDate(checkinDate.getValue() != null ? checkinDate.getValue().toString() + " 00:00:00" : LocalDateTime.now().format(fmt));
            e.setContractEndDate(contractEndDate.getValue() != null ? contractEndDate.getValue().toString() : "");
            e.setStatus("在住");
            ctx.getElderlyDao().insert(e);
            PersistentIdGenerator.getInstance().save();
            Map<String, Object> undoData = new HashMap<>();
            undoData.put("type", "elderly_checkin");
            undoData.put("elderlyId", e.getId());
            undoData.put("bedId", bedId);
            AuditLogger.logReversible("老人入住", "老人管理", e.getName(), undoData);
            refreshElderly(table, ctx.getElderlyDao().findAll());
            return e;
        });
        dlg.showAndWait();
    }

    private void showEditElderlyDialog(Elderly elder, TableView<Elderly> table) {
        // 快照旧值，用于撤销
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("type", "elderly_edit");
        snapshot.put("elderlyId", elder.getId());
        snapshot.put("oldName", elder.getName());
        snapshot.put("oldAge", elder.getAge());
        snapshot.put("oldGender", elder.getGender());
        snapshot.put("oldIdCard", elder.getIdCard());
        snapshot.put("oldBloodType", elder.getBloodType());
        snapshot.put("oldBirthDate", elder.getBirthDate());
        snapshot.put("oldPhone", elder.getPhone());
        snapshot.put("oldAddress", elder.getAddress());
        snapshot.put("oldFamilyMember", elder.getFamilyMember());
        snapshot.put("oldEmergencyContact", elder.getEmergencyContact());
        snapshot.put("oldEmergencyPhone", elder.getEmergencyPhone());
        snapshot.put("oldBedId", elder.getBedId());
        snapshot.put("oldCheckInDate", elder.getCheckInDate());
        snapshot.put("oldContractEndDate", elder.getContractEndDate());
        snapshot.put("oldRoomNo", elder.getRoomNo());
        snapshot.put("oldBuildingId", elder.getBuildingId());

        Dialog<Elderly> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("编辑老人信息 - " + elder.getName());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField name = new TextField(elder.getName() != null ? elder.getName() : "");
        TextField age = new TextField(String.valueOf(elder.getAge()));
        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("男", "女"); gender.setValue(elder.getGender() != null ? elder.getGender() : "男");
        TextField idCard = new TextField(elder.getIdCard() != null ? elder.getIdCard() : "");
        ComboBox<String> bloodType = new ComboBox<>();
        bloodType.getItems().addAll("A", "B", "AB", "O"); bloodType.setValue(elder.getBloodType() != null ? elder.getBloodType() : "A");
        DatePicker birthDate = new DatePicker();
        if (elder.getBirthDate() != null && !elder.getBirthDate().isEmpty()) {
            try { birthDate.setValue(LocalDate.parse(elder.getBirthDate().substring(0, 10))); } catch (Exception ex) {}
        }
        TextField phone = new TextField(elder.getPhone() != null ? elder.getPhone() : "");
        TextField address = new TextField(elder.getAddress() != null ? elder.getAddress() : "");
        TextField familyMember = new TextField(elder.getFamilyMember() != null ? elder.getFamilyMember() : "");
        TextField emContact = new TextField(elder.getEmergencyContact() != null ? elder.getEmergencyContact() : "");
        TextField emPhone = new TextField(elder.getEmergencyPhone() != null ? elder.getEmergencyPhone() : "");
        ComboBox<String> bedBox = new ComboBox<>();
        // 当前床位 + 空闲床位
        if (elder.getBedId() != null && !elder.getBedId().isEmpty()) {
            Bed curBed = ctx.getBedDao().findById(elder.getBedId());
            if (curBed != null) bedBox.getItems().add(curBed.getBedId() + " - " + curBed.getBedNo() + " (当前)");
        }
        ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus()))
            .forEach(b -> bedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
        if (!bedBox.getItems().isEmpty()) bedBox.setValue(bedBox.getItems().get(0));
        DatePicker checkinDate = new DatePicker();
        if (elder.getCheckInDate() != null && !elder.getCheckInDate().isEmpty()) {
            try { checkinDate.setValue(LocalDate.parse(elder.getCheckInDate().substring(0, 10))); } catch (Exception ex) {}
        }
        DatePicker contractEndDate = new DatePicker();
        if (elder.getContractEndDate() != null && !elder.getContractEndDate().isEmpty()) {
            try { contractEndDate.setValue(LocalDate.parse(elder.getContractEndDate().substring(0, 10))); } catch (Exception ex) {}
        }

        Label idCardMsg = new Label();
        idCardMsg.setStyle("-fx-font-size:11px");
        idCard.textProperty().addListener((o, ov, nv) -> {
            String result = validateIdCard(nv);
            if (result == null) {
                idCard.setStyle("-fx-border-color:red; -fx-border-width:1px");
                idCardMsg.setText(nv.length() == 18 ? "身份证格式错误" : "");
                idCardMsg.setStyle("-fx-text-fill:red; -fx-font-size:11px");
            } else {
                idCard.setStyle("");
                String birth = nv.substring(6, 14);
                LocalDate bd = LocalDate.parse(birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8));
                birthDate.setValue(bd);
                int calculatedAge = LocalDate.now().getYear() - bd.getYear();
                age.setText(String.valueOf(calculatedAge));
                int seqDigit = Integer.parseInt(nv.substring(14, 17));
                gender.setValue(seqDigit % 2 == 1 ? "男" : "女");
                idCardMsg.setText("✓ 出生: " + result + "  性别: " + gender.getValue() + "  年龄: " + calculatedAge);
                idCardMsg.setStyle("-fx-text-fill:green; -fx-font-size:11px");
            }
        });

        grid.add(new Label("姓名："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("年龄："), 0, 1); grid.add(age, 1, 1);
        grid.add(new Label("出生日期："), 2, 1); grid.add(birthDate, 3, 1);
        grid.add(new Label("性别："), 0, 2); grid.add(gender, 1, 2);
        grid.add(new Label("血型："), 2, 2); grid.add(bloodType, 3, 2);
        grid.add(new Label("身份证："), 0, 3); grid.add(idCard, 1, 3);
        grid.add(idCardMsg, 2, 3, 2, 1);
        grid.add(new Label("电话："), 0, 4); grid.add(phone, 1, 4);
        grid.add(new Label("地址："), 2, 4); grid.add(address, 3, 4);
        grid.add(new Label("家属："), 0, 5); grid.add(familyMember, 1, 5);
        grid.add(new Label("紧急联系人："), 2, 5); grid.add(emContact, 3, 5);
        grid.add(new Label("紧急电话："), 0, 6); grid.add(emPhone, 1, 6);
        grid.add(new Label("床位："), 2, 6); grid.add(bedBox, 3, 6);
        grid.add(new Label("入住日期："), 0, 7); grid.add(checkinDate, 1, 7);
        grid.add(new Label("合同到期："), 2, 7); grid.add(contractEndDate, 3, 7);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || name.getText().trim().isEmpty()) return null;
            String idCardText = idCard.getText().trim();
            if (!idCardText.isEmpty() && validateIdCard(idCardText) == null) return null;

            String bedSel = bedBox.getValue();
            if (bedSel != null) {
                String newBedId = bedSel.split(" - ")[0];
                boolean isCurrentBed = bedSel.contains("(当前)");
                if (!newBedId.equals(elder.getBedId())) {
                    // 释放旧床位
                    if (elder.getBedId() != null && !elder.getBedId().isEmpty()) {
                        Bed oldBed = ctx.getBedDao().findById(elder.getBedId());
                        if (oldBed != null) { oldBed.setStatus("available"); ctx.getBedDao().update(oldBed); }
                    }
                    // 占用新床位（非当前床位即新选择的空闲床位）
                    if (!isCurrentBed) {
                        Bed newBed = ctx.getBedDao().findById(newBedId);
                        if (newBed != null) { newBed.setStatus("occupied"); ctx.getBedDao().update(newBed); }
                    }
                    elder.setBedId(newBedId);
                    Bed bed = ctx.getBedDao().findById(newBedId);
                    if (bed != null) {
                        Room room = ctx.getRoomDao().findById(bed.getRoomId());
                        if (room != null) { elder.setRoomNo(room.getRoomNo()); elder.setBuildingId(room.getBuildingId()); }
                    }
                }
            }

            elder.setName(name.getText().trim());
            elder.setGender(gender.getValue());
            elder.setIdCard(idCardText);
            elder.setBloodType(bloodType.getValue());
            if (birthDate.getValue() != null) {
                elder.setBirthDate(birthDate.getValue().toString());
                elder.setAge(LocalDate.now().getYear() - birthDate.getValue().getYear());
            } else {
                try { elder.setAge(Integer.parseInt(age.getText())); } catch (Exception ex) { elder.setAge(0); }
            }
            elder.setPhone(phone.getText().trim());
            elder.setAddress(address.getText().trim());
            elder.setFamilyMember(familyMember.getText().trim());
            elder.setEmergencyContact(emContact.getText().trim());
            elder.setEmergencyPhone(emPhone.getText().trim());
            elder.setCheckInDate(checkinDate.getValue() != null ? checkinDate.getValue().toString() + " 00:00:00" : LocalDateTime.now().format(fmt));
            elder.setContractEndDate(contractEndDate.getValue() != null ? contractEndDate.getValue().toString() : "");
            ctx.getElderlyDao().update(elder);
            PersistentIdGenerator.getInstance().save();
            AuditLogger.logReversible("编辑老人信息", "老人管理", elder.getName(), snapshot);
            refreshElderly(table, ctx.getElderlyDao().findAll());
            return elder;
        });
        dlg.showAndWait();
    }

    private void showElderlyInfoDialog(Elderly e) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("老人信息 - " + e.getName());
        dlg.setResizable(true);

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(12); grid.setPadding(new Insets(20));

        String[] labels = {"姓名", "年龄", "性别", "血型", "身份证号", "出生日期",
            "电话", "地址", "家属", "紧急联系人", "紧急联系电话",
            "入住日期", "合同到期", "护理等级", "房间号", "状态"};
        String[] values = {
            nvl(e.getName()), String.valueOf(e.getAge()), nvl(e.getGender()), nvl(e.getBloodType()),
            nvl(e.getIdCard()), nvl(e.getBirthDate()),
            nvl(e.getPhone()), nvl(e.getAddress()), nvl(e.getFamilyMember()),
            nvl(e.getEmergencyContact()), nvl(e.getEmergencyPhone()),
            nvl(e.getCheckInDate()), nvl(e.getContractEndDate()),
            nvl(e.getNursingLevelCode()), nvl(e.getRoomNo()), nvl(e.getStatus())
        };

        for (int i = 0; i < labels.length; i++) {
            Label lbl = new Label(labels[i] + "：");
            lbl.setStyle("-fx-font-weight:bold; -fx-font-size:13px");
            Label val = new Label(values[i]);
            val.setStyle("-fx-font-size:13px");
            val.setWrapText(true);
            grid.add(lbl, 0, i);
            grid.add(val, 1, i);
        }

        // 显示当前床位号
        Label bedLbl = new Label("床位号：");
        bedLbl.setStyle("-fx-font-weight:bold; -fx-font-size:13px");
        String bedNo = "";
        if (e.getBedId() != null && !e.getBedId().isEmpty()) {
            Bed bed = ctx.getBedDao().findById(e.getBedId());
            if (bed != null) bedNo = bed.getBedNo();
        }
        grid.add(bedLbl, 0, labels.length);
        grid.add(new Label(bedNo), 1, labels.length);

        ButtonType closeBtn = new ButtonType("关闭", ButtonBar.ButtonData.CANCEL_CLOSE);
        dlg.getDialogPane().getButtonTypes().add(closeBtn);
        dlg.getDialogPane().setContent(grid);
        dlg.showAndWait();
    }

    private String nvl(String s) { return s != null && !s.isEmpty() ? s : "-"; }

    private void showSetLevelDialog(Elderly e) {
        boolean hasLevel = e.getNursingLevelCode() != null && !e.getNursingLevelCode().isEmpty();

        if (hasLevel) {
            // 已有护理级别 — 询问是否移除
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                e.getName() + " 当前护理级别为 " + e.getNursingLevelCode()
                + "。\n\n移除后将自动弹出新级别选择窗口。\n\n确定要移除吗？（会级联删除客户在当前级别的所有护理项目）",
                ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    removeCustomerLevel(e);
                    // 移除后立即弹出设置新级别
                    showSetLevelDialog(e);
                }
            });
        } else {
            // 没有护理级别 — 设置新级别
            showLevelSelection(e);
        }
    }

    private void showLevelSelection(Elderly e) {
        List<NursingLevel> levels = ctx.getNursingLevelDao().findAll().stream()
            .filter(l -> "启用".equals(l.getStatus())).toList();
        if (levels.isEmpty()) { LoginPane.showAlert(Alert.AlertType.WARNING, "没有可用的护理级别"); return; }
        ChoiceDialog<String> dlg = new ChoiceDialog<>(
            levels.get(0).getCode() + " - " + levels.get(0).getName(),
            levels.stream().map(l -> l.getCode() + " - " + l.getName()).toList());
        dlg.initOwner(stage);
        dlg.setTitle("设置护理等级");
        dlg.setHeaderText("为 " + e.getName() + " 设置护理等级");
        dlg.showAndWait().ifPresent(sel -> {
            String code = sel.split(" - ")[0];
            String oldLevelCode = e.getNursingLevelCode();
            e.setNursingLevelCode(code);
            ctx.getElderlyDao().update(e);
            // 批量添加该级别的护理项目
            String now = LocalDateTime.now().format(fmt);
            String expireDate = LocalDate.now().plusMonths(3).toString();
            List<NursingContent> contents = ctx.getNursingContentDao().findByLevelCode(code);
            List<String> addedProjectIds = new ArrayList<>();
            for (NursingContent nc : contents) {
                CustomerCareProject ccp = new CustomerCareProject(null, e.getId(),
                    nc.getCareProjectCode(), 1, now, expireDate);
                ctx.getCustomerCareProjectDao().insert(ccp);
                addedProjectIds.add(ccp.getId());
            }
            PersistentIdGenerator.getInstance().save();
            Map<String, Object> undoData = new HashMap<>();
            undoData.put("type", "nursing_level_set");
            undoData.put("elderlyId", e.getId());
            undoData.put("oldLevelCode", oldLevelCode != null ? oldLevelCode : "");
            undoData.put("newLevelCode", code);
            undoData.put("addedProjectIds", addedProjectIds);
            AuditLogger.logReversible("设置护理等级", "老人管理", e.getName() + " → " + code, undoData);
        });
    }

    private void removeCustomerLevel(Elderly e) {
        String oldLevelCode = e.getNursingLevelCode();
        // 删除前保存已购护理项目信息用于撤销
        List<CustomerCareProject> owned = ctx.getCustomerCareProjectDao().findByCustomerId(e.getId());
        List<Map<String, String>> removedProjects = new ArrayList<>();
        for (CustomerCareProject ccp : owned) {
            Map<String, String> info = new HashMap<>();
            info.put("projectCode", ccp.getProjectCode());
            info.put("quantity", String.valueOf(ccp.getQuantity()));
            info.put("purchaseDate", ccp.getPurchaseDate());
            info.put("expireDate", ccp.getExpireDate());
            removedProjects.add(info);
        }
        e.setNursingLevelCode(null);
        ctx.getElderlyDao().update(e);
        for (CustomerCareProject ccp : owned) {
            ctx.getCustomerCareProjectDao().delete(ccp.getId());
        }
        PersistentIdGenerator.getInstance().save();
        Map<String, Object> undoData = new HashMap<>();
        undoData.put("type", "nursing_level_remove");
        undoData.put("elderlyId", e.getId());
        undoData.put("oldLevelCode", oldLevelCode != null ? oldLevelCode : "");
        undoData.put("removedProjects", removedProjects);
        AuditLogger.logReversible("移除护理级别", "老人管理", e.getName(), undoData);
    }

    // ==================== 床位管理 ====================
    private VBox buildBedManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        // 统计卡片
        List<Bed> allBeds = ctx.getBedDao().findAll();
        long total = allBeds.size();
        long available = allBeds.stream().filter(b -> "available".equals(b.getStatus())).count();
        long occupied = allBeds.stream().filter(b -> "occupied".equals(b.getStatus())).count();
        long outCount = allBeds.stream().filter(b -> "out".equals(b.getStatus())).count();

        GridPane statRow = new GridPane();
        statRow.setHgap(15);
        Label totalLabel = new Label("总床位: " + total);
        Label availLabel = new Label("空闲: " + available);
        Label occupLabel = new Label("有人: " + occupied);
        Label outLabel = new Label("外出: " + outCount);
        for (Label l : new Label[]{totalLabel, availLabel, occupLabel, outLabel}) {
            l.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-padding:8 15 8 15; "
                + "-fx-background-color:#ecf0f1; -fx-background-radius:6");
        }
        statRow.add(totalLabel, 0, 0); statRow.add(availLabel, 1, 0);
        statRow.add(occupLabel, 2, 0); statRow.add(outLabel, 3, 0);

        // 楼层选择
        HBox filterRow = new HBox(10);
        ComboBox<String> floorBox = new ComboBox<>();
        floorBox.getItems().add("全部");
        for (int i = 1; i <= 6; i++) floorBox.getItems().add(i + "楼");
        floorBox.setValue("全部");

        Button addBedBtn = new Button("添加床位");
        Button delBedBtn = new Button("删除床位");
        Button swapBedBtn = new Button("床位调换");
        Button roomBtn = new Button("管理房间");

        filterRow.getChildren().addAll(new Label("楼层："), floorBox, addBedBtn, delBedBtn, swapBedBtn, roomBtn);


        // 床位展示区
        VBox bedArea = new VBox(8);
        bedArea.setPadding(new Insets(5));

        Runnable refreshBedArea = () -> {
            bedArea.getChildren().clear();
            String floorSel = floorBox.getValue() != null ? floorBox.getValue() : "全部";
            Building building = ctx.getBuildingDao().findAll().stream().findFirst().orElse(null);
            if (building == null) { bedArea.getChildren().add(new Label("无楼栋数据")); return; }
            List<Room> rooms = ctx.getRoomDao().findByBuildingId(building.getBuildingId());
            if (!"全部".equals(floorSel)) {
                int floor = Integer.parseInt(floorSel.replace("楼", ""));
                rooms = rooms.stream().filter(r -> r.getFloor() == floor).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
            }
            if (rooms.isEmpty()) { bedArea.getChildren().add(new Label("无房间")); return; }
            // 按床位占用状态排序：有空闲床位的房间在前，全占用的在后
            rooms.sort((a, b) -> {
                List<Bed> ba = ctx.getBedDao().findByRoomId(a.getRoomId());
                List<Bed> bb = ctx.getBedDao().findByRoomId(b.getRoomId());
                boolean aHasAvail = ba.stream().anyMatch(bd -> "available".equals(bd.getStatus()));
                boolean bHasAvail = bb.stream().anyMatch(bd -> "available".equals(bd.getStatus()));
                if (aHasAvail && !bHasAvail) return -1;
                if (!aHasAvail && bHasAvail) return 1;
                return a.getRoomNo().compareTo(b.getRoomNo());
            });
            for (Room room : rooms) {
                List<Bed> beds = ctx.getBedDao().findByRoomId(room.getRoomId());
                // 空闲在前，占用在后
                beds.sort((a, b) -> Integer.compare(
                    "available".equals(a.getStatus()) ? 0 : "out".equals(a.getStatus()) ? 1 : 2,
                    "available".equals(b.getStatus()) ? 0 : "out".equals(b.getStatus()) ? 1 : 2));
                // 每个房间一个区块
                VBox roomBlock = new VBox(6);
                roomBlock.setPadding(new Insets(4, 0, 8, 0));
                Label roomLabel = new Label(room.getRoomNo() + "（" + beds.size() + "床）");
                roomLabel.setStyle("-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill:#1a5276; -fx-padding:2 0");
                roomBlock.getChildren().add(roomLabel);
                if (beds.isEmpty()) {
                    roomBlock.getChildren().add(new Label("  （无床位）"));
                } else {
                    FlowPane bedFlow = new FlowPane(8, 8);
                    for (Bed bed : beds) {
                        VBox bedTile = new VBox(2);
                        bedTile.setAlignment(Pos.CENTER);
                        bedTile.setPrefWidth(100);
                        bedTile.setPadding(new Insets(8, 10, 8, 10));
                        Label bedNoLabel = new Label(bed.getBedNo());
                        bedNoLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold");
                        String statusText = "available".equals(bed.getStatus()) ? "空闲" : "occupied".equals(bed.getStatus()) ? "占用" : "out".equals(bed.getStatus()) ? "外出" : bed.getStatus();
                        Label statusTag = new Label(statusText);
                        statusTag.setStyle("-fx-font-size:11px; -fx-font-weight:bold");
                        bedTile.getChildren().addAll(bedNoLabel, statusTag);
                        switch (bed.getStatus()) {
                            case "available":
                                bedTile.setStyle("-fx-background-color:#27ae60; -fx-background-radius:8; -fx-border-color:#1e8449; -fx-border-radius:8; -fx-border-width:2");
                                bedNoLabel.setStyle(bedNoLabel.getStyle() + " -fx-text-fill:#1a1a2e");
                                break;
                            case "occupied":
                                bedTile.setStyle("-fx-background-color:#c0392b; -fx-background-radius:8; -fx-border-color:#922b21; -fx-border-radius:8; -fx-border-width:3");
                                bedNoLabel.setStyle(bedNoLabel.getStyle() + " -fx-text-fill:#ffffff");
                                statusTag.setStyle(statusTag.getStyle() + " -fx-text-fill:#f5b7b1");
                                break;
                            case "out":
                                bedTile.setStyle("-fx-background-color:#e67e22; -fx-background-radius:8; -fx-border-color:#ca6f1e; -fx-border-radius:8; -fx-border-width:2");
                                bedNoLabel.setStyle(bedNoLabel.getStyle() + " -fx-text-fill:#1a1a2e");
                                break;
                            default:
                                bedTile.setStyle("-fx-background-color:#7f8c8d; -fx-background-radius:8; -fx-border-color:#6c7a7a; -fx-border-radius:8; -fx-border-width:2");
                                bedNoLabel.setStyle(bedNoLabel.getStyle() + " -fx-text-fill:#1a1a2e");
                                break;
                        }
                        bedFlow.getChildren().add(bedTile);
                    }
                    roomBlock.getChildren().add(bedFlow);
                }
                bedArea.getChildren().add(roomBlock);
            }
        };
        refreshBedArea.run();
        floorBox.setOnAction(e -> refreshBedArea.run());

        addBedBtn.setOnAction(e -> {
            Building building = ctx.getBuildingDao().findAll().stream().findFirst().orElse(null);
            if (building == null) return;
            showAddBedDialog(building.getBuildingId(), refreshBedArea);
        });

        delBedBtn.setOnAction(e -> {
            List<Bed> availBeds = ctx.getBedDao().findAll().stream()
                .filter(b -> "available".equals(b.getStatus())).toList();
            if (availBeds.isEmpty()) { LoginPane.showAlert(Alert.AlertType.WARNING, "没有可删除的空闲床位"); return; }
            ChoiceDialog<String> dlg = new ChoiceDialog<>(
                availBeds.get(0).getBedId() + " - " + availBeds.get(0).getBedNo(),
                availBeds.stream().map(b -> b.getBedId() + " - " + b.getBedNo()).toList());
            dlg.initOwner(stage);
            dlg.setTitle("删除床位");
            dlg.setHeaderText("选择要删除的空闲床位（占用/外出中的床位不可删除）");
            dlg.showAndWait().ifPresent(sel -> {
                String bedId = sel.split(" - ")[0];
                ctx.getBedDao().delete(bedId);
                PersistentIdGenerator.getInstance().save();
                refreshBedArea.run();
            });
        });

        swapBedBtn.setOnAction(e -> showSwapBedDialog(refreshBedArea));
        roomBtn.setOnAction(e -> { showRoomManageDialog(); refreshBedArea.run(); });

        ScrollPane scrollPane = new ScrollPane(bedArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent; -fx-background:transparent");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        box.getChildren().addAll(statRow, filterRow, scrollPane);
        return box;
    }

    private void showAddBedDialog(String buildingId, Runnable onDone) {
        List<Room> rooms = ctx.getRoomDao().findByBuildingId(buildingId);
        if (rooms.isEmpty()) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先创建房间"); return; }

        ChoiceDialog<String> dlg = new ChoiceDialog<>(
            rooms.get(0).getRoomId() + " - " + rooms.get(0).getRoomNo(),
            rooms.stream().map(r -> r.getRoomId() + " - " + r.getRoomNo() + " (" + r.getRoomType()
                + " " + ctx.getBedDao().findByRoomId(r.getRoomId()).size() + "/" + r.getCapacity() + ")").toList());
        dlg.initOwner(stage);
        dlg.setTitle("选择房间");
        dlg.setHeaderText("请选择要添加床位的房间");

        dlg.showAndWait().ifPresent(sel -> {
            String roomId = sel.split(" - ")[0];
            Room room = ctx.getRoomDao().findById(roomId);
            int currentBeds = ctx.getBedDao().findByRoomId(roomId).size();
            if (room != null && currentBeds >= room.getCapacity()) {
                LoginPane.showAlert(Alert.AlertType.WARNING,
                    "该房间容量已满（" + currentBeds + "/" + room.getCapacity() + "），无法再添加床位");
                return;
            }
            // 获取已有床位号集合
            java.util.Set<String> existingNos = new java.util.HashSet<>();
            ctx.getBedDao().findAll().forEach(b -> existingNos.add(b.getBedNo()));

            String roomNo = room.getRoomNo();
            TextInputDialog bedDlg = new TextInputDialog();
            bedDlg.setTitle("添加床位");
            bedDlg.setHeaderText("请输入床位号（当前 " + currentBeds + "/" + room.getCapacity() + "）");
            bedDlg.showAndWait().ifPresent(bedNo -> {
                String trimmed = bedNo.trim();
                if (trimmed.isEmpty()) return;
                // 如果输入不含"-"，自动补齐为"房间号-床位号"
                if (!trimmed.contains("-")) {
                    trimmed = roomNo + "-" + trimmed;
                }
                if (existingNos.contains(trimmed)) {
                    LoginPane.showAlert(Alert.AlertType.WARNING, "床位号「" + trimmed + "」已存在，请使用其他编号");
                    return;
                }
                Bed bed = new Bed();
                bed.setRoomId(roomId);
                bed.setBedNo(trimmed);
                bed.setStatus("available");
                ctx.getBedDao().insert(bed);
                PersistentIdGenerator.getInstance().save();
                onDone.run();
            });
        });
    }

    private void showRoomManageDialog() {
        Building building = ctx.getBuildingDao().findAll().stream().findFirst().orElse(null);
        if (building == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先创建楼栋"); return; }

        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("房间管理 — " + building.getBuildingName());
        dlg.setResizable(true);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        TableView<Room> table = new TableView<>();
        TableColumn<Room, String> r1 = tc("楼层", "floor");
        TableColumn<Room, String> r2 = tc("房间号", "roomNo");
        TableColumn<Room, String> r3 = tc("类型", "roomType");
        TableColumn<Room, String> r4 = tc("容量", "capacity");
        TableColumn<Room, String> r5 = tc("月费", "price");
        table.getColumns().addAll(r1, r2, r3, r4, r5);

        Runnable refreshRooms = () -> {
            List<Room> rooms = ctx.getRoomDao().findByBuildingId(building.getBuildingId());
            rooms.sort((a, b) -> {
                int fa = a.getFloor() != 0 ? a.getFloor() : Integer.MAX_VALUE;
                int fb = b.getFloor() != 0 ? b.getFloor() : Integer.MAX_VALUE;
                if (fa != fb) return fa - fb;
                String na = a.getRoomNo() != null ? a.getRoomNo() : "";
                String nb = b.getRoomNo() != null ? b.getRoomNo() : "";
                return na.compareTo(nb);
            });
            refreshTable(table, rooms);
        };
        refreshRooms.run();

        Button addBtn = new Button("添加房间");
        Button delBtn = new Button("删除房间");

        addBtn.setOnAction(e -> {
            Dialog<Void> addDlg = new Dialog<>();
            addDlg.initOwner(stage);
            addDlg.setTitle("添加房间");
            GridPane g = new GridPane();
            g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(15));
            ComboBox<Integer> floorSel = new ComboBox<>();
            for (int i = 1; i <= building.getFloorCount(); i++) floorSel.getItems().add(i);
            floorSel.setValue(1);
            TextField roomNo = new TextField();
            ComboBox<String> typeSel = new ComboBox<>();
            typeSel.getItems().addAll("单人间", "双人间", "三人间");
            typeSel.setValue("双人间");
            TextField capacity = new TextField("2");
            TextField price = new TextField("3000");

            g.add(new Label("楼层："), 0, 0); g.add(floorSel, 1, 0);
            g.add(new Label("房间号："), 0, 1); g.add(roomNo, 1, 1);
            g.add(new Label("类型："), 0, 2); g.add(typeSel, 1, 2);
            g.add(new Label("容量："), 0, 3); g.add(capacity, 1, 3);
            g.add(new Label("月费："), 0, 4); g.add(price, 1, 4);

            addDlg.getDialogPane().setContent(g);
            ButtonType ok = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
            addDlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
            addDlg.setResultConverter(b -> {
                if (b == ok && !roomNo.getText().trim().isEmpty()) {
                    Room room = new Room();
                    room.setBuildingId(building.getBuildingId());
                    room.setFloor(floorSel.getValue());
                    room.setRoomNo(roomNo.getText().trim());
                    room.setRoomType(typeSel.getValue());
                    try { room.setCapacity(Integer.parseInt(capacity.getText())); } catch (Exception ex) { room.setCapacity(2); }
                    try { room.setPrice(Double.parseDouble(price.getText())); } catch (Exception ex) { room.setPrice(3000); }
                    room.setStatus("active");
                    ctx.getRoomDao().insert(room);
                    PersistentIdGenerator.getInstance().save();
                    refreshRooms.run();
                }
                return null;
            });
            addDlg.showAndWait();
        });

        delBtn.setOnAction(e -> {
            Room sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择房间"); return; }
            // 检查是否有床位
            List<Bed> beds = ctx.getBedDao().findByRoomId(sel.getRoomId());
            if (!beds.isEmpty()) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "该房间还有 " + beds.size() + " 个床位，请先删除床位");
                return;
            }
            ctx.getRoomDao().delete(sel.getRoomId());
            PersistentIdGenerator.getInstance().save();
            refreshRooms.run();
        });

        HBox btnRow = new HBox(10, addBtn, delBtn);
        content.getChildren().addAll(btnRow, table);
        dlg.getDialogPane().setContent(content);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }

    private void showSwapBedDialog(Runnable onDone) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("床位调换 — 当天调换当天办理");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        ComboBox<String> customerBox = new ComboBox<>();
        ctx.getElderlyDao().findAll().stream().filter(e -> "在住".equals(e.getStatus()))
            .forEach(e -> customerBox.getItems().add(e.getId() + " - " + e.getName()));
        customerBox.setPromptText("选择客户");

        ComboBox<String> newBedBox = new ComboBox<>();
        newBedBox.setPromptText("选择新床位");

        // 楼栋+楼层选择
        ComboBox<String> buildingBox = new ComboBox<>();
        ctx.getBuildingDao().findAll().forEach(b -> buildingBox.getItems().add(b.getBuildingId() + " " + b.getBuildingName()));
        ComboBox<Integer> floorBox = new ComboBox<>();
        for (int i = 1; i <= 6; i++) floorBox.getItems().add(i);
        ComboBox<String> roomBox = new ComboBox<>();
        roomBox.setPromptText("先选楼层");

        buildingBox.setOnAction(e -> {
            roomBox.getItems().clear();
            newBedBox.getItems().clear();
        });
        floorBox.setOnAction(e -> {
            roomBox.getItems().clear();
            newBedBox.getItems().clear();
            String selB = buildingBox.getValue();
            if (selB != null && floorBox.getValue() != null) {
                String bid = selB.split(" ")[0];
                ctx.getRoomDao().findByBuildingId(bid).stream()
                    .filter(r -> r.getFloor() == floorBox.getValue())
                    .forEach(r -> roomBox.getItems().add(r.getRoomId() + " - " + r.getRoomNo()));
            }
        });
        roomBox.setOnAction(e -> {
            newBedBox.getItems().clear();
            String selR = roomBox.getValue();
            if (selR != null) {
                String roomId = selR.split(" - ")[0];
                ctx.getBedDao().findByRoomId(roomId).stream()
                    .filter(b -> "available".equals(b.getStatus()))
                    .forEach(b -> newBedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
            }
        });

        // 默认选中第一个楼栋和1楼，触发房间列表加载
        if (!buildingBox.getItems().isEmpty()) buildingBox.setValue(buildingBox.getItems().get(0));
        floorBox.setValue(1);

        grid.add(new Label("客户："), 0, 0); grid.add(customerBox, 1, 0);
        grid.add(new Label("楼栋："), 0, 1); grid.add(buildingBox, 1, 1);
        grid.add(new Label("楼层："), 0, 2); grid.add(floorBox, 1, 2);
        grid.add(new Label("房间："), 0, 3); grid.add(roomBox, 1, 3);
        grid.add(new Label("新床位："), 0, 4); grid.add(newBedBox, 1, 4);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("调换", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == okBtn && customerBox.getValue() != null && newBedBox.getValue() != null) {
                String customerId = customerBox.getValue().split(" - ")[0];
                String newBedId = newBedBox.getValue().split(" - ")[0];
                Elderly elder = ctx.getElderlyDao().findById(customerId);
                Bed oldBed = elder.getBedId() != null ? ctx.getBedDao().findById(elder.getBedId()) : null;
                Bed newBed = ctx.getBedDao().findById(newBedId);

                if (elder == null || newBed == null) return null;

                // 快照旧床位信息用于撤销
                String oldBedId = elder.getBedId();
                String oldRoomNo = elder.getRoomNo();
                String oldBuildingId = elder.getBuildingId();

                // 旧床位改为空闲
                if (oldBed != null) oldBed.setStatus("available");
                // 新床位改为有人
                newBed.setStatus("occupied");
                // 更新客户信息
                elder.setBedId(newBedId);
                Room newRoom = ctx.getRoomDao().findById(newBed.getRoomId());
                if (newRoom != null) {
                    elder.setRoomNo(newRoom.getRoomNo());
                    elder.setBuildingId(newRoom.getBuildingId());
                }
                ctx.getElderlyDao().update(elder);
                PersistentIdGenerator.getInstance().save();
                onDone.run();
                Map<String, Object> undoData = new HashMap<>();
                undoData.put("type", "bed_swap");
                undoData.put("elderlyId", customerId);
                undoData.put("oldBedId", oldBedId != null ? oldBedId : "");
                undoData.put("newBedId", newBedId);
                undoData.put("oldRoomNo", oldRoomNo != null ? oldRoomNo : "");
                undoData.put("oldBuildingId", oldBuildingId != null ? oldBuildingId : "");
                AuditLogger.logReversible("床位调换", "床位管理", customerId + " → 床位 " + newBed.getBedNo(), undoData);
            }
            return null;
        });
        dlg.showAndWait();
    }

    private void showAddBedDialog(TableView<Bed> table, String buildingInfo) {
        if (buildingInfo == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择楼栋"); return; }
        String buildingId = buildingInfo.split(" ")[0];
        List<Room> rooms = ctx.getRoomDao().findByBuildingId(buildingId);

        ChoiceDialog<String> dlg = new ChoiceDialog<>(
            rooms.get(0).getRoomId() + " - " + rooms.get(0).getRoomNo(),
            rooms.stream().map(r -> r.getRoomId() + " - " + r.getRoomNo() + " (" + r.getRoomType() + ")").toList());
        dlg.initOwner(stage);
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
        TableColumn<NursingLevel, String> c4 = tc("状态", "status");
        TableColumn<NursingLevel, String> c5 = tc("频率", "frequency");
        table.getColumns().addAll(c1, c2, c3, c4, c5);
        refreshTable(table, ctx.getNursingLevelDao().findAll());

        Button addBtn = new Button("新增护理等级");
        Button configBtn = new Button("配置护理项目");
        Button toggleBtn = new Button("启用/停用");

        addBtn.setOnAction(e -> {
            Dialog<NursingLevel> dlg = nursingLevelDialog(null);
            dlg.showAndWait().ifPresent(l -> {
                ctx.getNursingLevelDao().insert(l);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getNursingLevelDao().findAll());
            });
        });

        configBtn.setOnAction(e -> {
            NursingLevel level = table.getSelectionModel().getSelectedItem();
            if (level == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择护理等级"); return; }
            showLevelProjectConfig(level);
        });

        toggleBtn.setOnAction(e -> {
            NursingLevel level = table.getSelectionModel().getSelectedItem();
            if (level == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择护理等级"); return; }
            level.setStatus("启用".equals(level.getStatus()) ? "停用" : "启用");
            ctx.getNursingLevelDao().update(level);
            PersistentIdGenerator.getInstance().save();
            refreshTable(table, ctx.getNursingLevelDao().findAll());
        });

        HBox btns = new HBox(10, addBtn, configBtn, toggleBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    private void showLevelProjectConfig(NursingLevel level) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("配置 " + level.getCode() + " - " + level.getName() + " 的护理项目");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // 已配置项目
        Label assignedLabel = new Label("已配置的护理项目：");
        assignedLabel.setStyle("-fx-font-weight:bold");
        TableView<NursingContent> assignedTable = new TableView<>();
        TableColumn<NursingContent, String> ac1 = tc("项目代码", "careProjectCode");
        TableColumn<NursingContent, String> ac2 = tc("内容名称", "contentName");
        assignedTable.getColumns().addAll(ac1, ac2);
        Runnable refreshAssigned = () -> refreshTable(assignedTable, ctx.getNursingContentDao().findByLevelCode(level.getCode()));
        refreshAssigned.run();

        Button removeBtn = new Button("移除选中项目");
        removeBtn.setOnAction(e -> {
            NursingContent nc = assignedTable.getSelectionModel().getSelectedItem();
            if (nc == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要移除的项目"); return; }
            ctx.getNursingContentDao().delete(nc.getContentId());
            PersistentIdGenerator.getInstance().save();
            refreshAssigned.run();
        });

        // 可添加项目
        Label availLabel = new Label("可添加的护理项目：");
        availLabel.setStyle("-fx-font-weight:bold");
        TableView<CareProject> availTable = new TableView<>();
        TableColumn<CareProject, String> vc1 = tc("代码", "code");
        TableColumn<CareProject, String> vc2 = tc("名称", "name");
        availTable.getColumns().addAll(vc1, vc2);

        Runnable refreshAvail = () -> {
            List<NursingContent> assigned = ctx.getNursingContentDao().findByLevelCode(level.getCode());
            List<CareProject> available = ctx.getCareProjectDao().findAll().stream()
                .filter(p -> "启用".equals(p.getStatus()))
                .filter(p -> assigned.stream().noneMatch(a -> a.getCareProjectCode().equals(p.getCode())))
                .toList();
            availTable.getItems().setAll(available);
        };
        refreshAvail.run();

        Button addBtn = new Button("添加选中项目");
        addBtn.setOnAction(e -> {
            CareProject cp = availTable.getSelectionModel().getSelectedItem();
            if (cp == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要添加的项目"); return; }
            NursingContent nc = new NursingContent();
            nc.setContentName(cp.getName());
            nc.setCareProjectCode(cp.getCode());
            nc.setNursingLevelCode(level.getCode());
            nc.setDescription(cp.getRemark());
            ctx.getNursingContentDao().insert(nc);
            PersistentIdGenerator.getInstance().save();
            refreshAssigned.run();
            refreshAvail.run();
        });

        content.getChildren().addAll(assignedLabel, assignedTable, removeBtn,
            new Separator(), availLabel, availTable, addBtn);
        dlg.getDialogPane().setContent(content);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }

    private Dialog<NursingLevel> nursingLevelDialog(NursingLevel existing) {
        Dialog<NursingLevel> dlg = new Dialog<>();
        dlg.initOwner(stage);
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
            return new NursingLevel(code.getText().trim(), name.getText().trim(), desc.getText().trim(), freq.getText().trim(), "启用");
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
        TableColumn<CareProject, String> c4 = tc("单位", "unit");
        TableColumn<CareProject, String> c5 = tc("价格", "price");
        TableColumn<CareProject, String> c6 = tc("周期", "cycle");
        TableColumn<CareProject, String> c7 = tc("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);

        // 定义排序规则：LZ < YL < KF，同前缀按数字升序
        java.util.Comparator<CareProject> projectComparator = (p1, p2) -> {
            String code1 = p1.getCode();
            String code2 = p2.getCode();

            if (code1 == null || code2 == null) return 0;

            // 提取前缀
            String prefix1 = code1.contains("-") ? code1.split("-")[0] : code1;
            String prefix2 = code2.contains("-") ? code2.split("-")[0] : code2;

            // 定义前缀优先级：LZ=1, YL=2, KF=3, 其他=4
            int priority1 = switch (prefix1) {
                case "LZ" -> 1;
                case "YL" -> 2;
                case "KF" -> 3;
                default -> 4;
            };
            int priority2 = switch (prefix2) {
                case "LZ" -> 1;
                case "YL" -> 2;
                case "KF" -> 3;
                default -> 4;
            };

            // 先按前缀优先级排序
            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }

            // 前缀相同，按数字部分升序
            try {
                int num1 = code1.contains("-") ? Integer.parseInt(code1.split("-")[1]) : 0;
                int num2 = code2.contains("-") ? Integer.parseInt(code2.split("-")[1]) : 0;
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                return code1.compareTo(code2);
            }
        };

        // 使用排序后的列表刷新表格
        Runnable refreshSortedTable = () -> {
            List<CareProject> sortedList = ctx.getCareProjectDao().findAll().stream()
                .sorted(projectComparator)
                .toList();
            refreshTable(table, sortedList);
        };

        refreshSortedTable.run();

        Button addBtn = new Button("新增护理项目");
        Button editBtn = new Button("编辑护理项目");
        Button delBtn = new Button("删除护理项目");
        Button toggleBtn = new Button("启用/停用");

        addBtn.setOnAction(e -> {
            Dialog<CareProject> dlg = careProjectDialog(null);
            dlg.showAndWait().ifPresent(p -> {
                ctx.getCareProjectDao().insert(p);
                PersistentIdGenerator.getInstance().save();
                refreshSortedTable.run();
                LoginPane.showAlert(Alert.AlertType.INFORMATION, "护理项目添加成功");
            });
        });

        editBtn.setOnAction(e -> {
            CareProject sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要编辑的护理项目");
                return;
            }
            Dialog<CareProject> dlg = careProjectDialog(sel);
            dlg.showAndWait().ifPresent(p -> {
                ctx.getCareProjectDao().update(p);
                PersistentIdGenerator.getInstance().save();
                refreshSortedTable.run();
                LoginPane.showAlert(Alert.AlertType.INFORMATION, "护理项目修改成功");
            });
        });

        delBtn.setOnAction(e -> {
            CareProject sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要删除的护理项目");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.initOwner(stage);
            confirm.setTitle("删除确认");
            confirm.setHeaderText("确定要删除护理项目「" + sel.getName() + "」吗？");
            confirm.setContentText("项目代码：" + sel.getCode() + "\n此操作不可撤销！");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    ctx.getCareProjectDao().delete(sel.getCode());
                    PersistentIdGenerator.getInstance().save();
                    refreshSortedTable.run();
                    LoginPane.showAlert(Alert.AlertType.INFORMATION, "护理项目删除成功");
                }
            });
        });

        toggleBtn.setOnAction(e -> {
            CareProject sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择护理项目");
                return;
            }
            String newStatus = "启用".equals(sel.getStatus()) ? "停用" : "启用";
            sel.setStatus(newStatus);
            ctx.getCareProjectDao().update(sel);
            PersistentIdGenerator.getInstance().save();
            refreshSortedTable.run();
            LoginPane.showAlert(Alert.AlertType.INFORMATION, "已将项目设置为：" + newStatus);
        });

        HBox btns = new HBox(10, addBtn, editBtn, delBtn, toggleBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

        private Dialog<CareProject> careProjectDialog(CareProject existing) {
        Dialog<CareProject> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle(existing == null ? "新增护理项目" : "编辑护理项目");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField name = new TextField();
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("生活照料", "医疗护理", "康复心理");
        categoryBox.setValue("生活照料");

        TextField code = new TextField();
        code.setEditable(false);
        code.setPromptText("自动生成");

        TextField unit = new TextField();
        unit.setPromptText("如：次、小时、天");
        TextField price = new TextField();
        price.setPromptText("0");
        TextField cycle = new TextField();
        cycle.setPromptText("如：每天、每周、按需");
        TextField remark = new TextField();
        remark.setPromptText("备注说明");

        // 根据名称和类别自动生成代码
        Runnable generateCode = () -> {
            if (existing != null) return; // 编辑时不自动生成

            String categoryName = categoryBox.getValue();
            if (categoryName == null) return;

            // 根据类别确定前缀
            String prefix = switch (categoryName) {
                case "生活照料" -> "LZ";
                case "医疗护理" -> "YL";
                case "康复心理" -> "KF";
                default -> "QT";
            };

            // 查找该类别下最大的序号
            int maxSeq = 0;
            for (CareProject p : ctx.getCareProjectDao().findAll()) {
                String pCode = p.getCode();
                if (pCode != null && pCode.startsWith(prefix + "-")) {
                    try {
                        int seq = Integer.parseInt(pCode.substring(3));
                        if (seq > maxSeq) {
                            maxSeq = seq;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // 生成新代码：前缀-(最大序号+1)
            String newCode = String.format("%s-%03d", prefix, maxSeq + 1);
            code.setText(newCode);
        };

        // 类别改变时重新生成代码
        categoryBox.setOnAction(e -> generateCode.run());

        // 初始化时生成代码
        if (existing == null) {
            generateCode.run();
        } else {
            // 编辑模式：填充现有数据
            name.setText(existing.getName());
            categoryBox.setValue(existing.getCategory());
            code.setText(existing.getCode());
            code.setEditable(true); // 编辑时允许修改代码
            unit.setText(existing.getUnit());
            price.setText(String.valueOf(existing.getPrice()));
            cycle.setText(existing.getCycle());
            remark.setText(existing.getRemark());
        }

        grid.add(new Label("项目名称："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("项目类别："), 0, 1); grid.add(categoryBox, 1, 1);
        grid.add(new Label("项目代码："), 0, 2); grid.add(code, 1, 2);
        grid.add(new Label("单位："), 0, 3); grid.add(unit, 1, 3);
        grid.add(new Label("价格："), 0, 4); grid.add(price, 1, 4);
        grid.add(new Label("周期："), 0, 5); grid.add(cycle, 1, 5);
        grid.add(new Label("备注："), 0, 6); grid.add(remark, 1, 6);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || name.getText().trim().isEmpty()) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "请填写项目名称");
                return null;
            }

            String codeText = code.getText().trim();

            // 检查代码是否为空
            if (codeText.isEmpty()) {
                LoginPane.showAlert(Alert.AlertType.ERROR, "项目代码不能为空！");
                return null;
            }

            // 检查代码格式（建议格式：前缀-序号，如 LZ-001）
            if (!codeText.matches("^[A-Z]{2}-\\d{3}$")) {
                LoginPane.showAlert(Alert.AlertType.ERROR,
                    "项目代码格式不正确！\n建议使用格式：前缀-序号（如 LZ-001、YL-002）\n前缀为2个大写字母，序号为3位数字");
                return null;
            }

            // 检查代码是否已存在（编辑时排除自己）
            CareProject existingProject = ctx.getCareProjectDao().findByCode(codeText);
            if (existingProject != null && (existing == null || !existingProject.getCode().equals(existing.getCode()))) {
                LoginPane.showAlert(Alert.AlertType.ERROR,
                    "项目代码「" + codeText + "」已存在，请使用其他代码！");
                return null;
            }

            try {
                double p = Double.parseDouble(price.getText().isEmpty() ? "0" : price.getText());
                return new CareProject(codeText, name.getText().trim(), categoryBox.getValue(),
                    unit.getText().trim(), p, cycle.getText().trim(), 1, "启用", remark.getText().trim());
            } catch (NumberFormatException ex) {
                LoginPane.showAlert(Alert.AlertType.ERROR, "价格格式不正确，请输入数字！");
                return null;
            }
        });
        return dlg;
    }


    // ==================== 护理记录 ====================
    private VBox buildNursingRecords() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CareRecord> table = new TableView<>();
        TableColumn<CareRecord, String> c0 = new TableColumn<>("老人姓名");
        c0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getElderlyId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getElderlyId());
        });
        TableColumn<CareRecord, String> c2 = tc("项目代码", "projectCode");
        TableColumn<CareRecord, String> c3 = tc("执行时间", "executeTime");
        TableColumn<CareRecord, String> c4 = tc("数量", "quantity");
        TableColumn<CareRecord, String> c5 = tc("护工", "nurseName");
        table.getColumns().addAll(c0, c2, c3, c4, c5);
        refreshTable(table, ctx.getCareRecordDao().findAll());

        table.setRowFactory(tv -> {
            TableRow<CareRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Elderly e = ctx.getElderlyDao().findById(row.getItem().getElderlyId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        box.getChildren().add(table);
        return box;
    }

    // ==================== 服务关注 ====================
    private VBox buildServiceFocus() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        // 客户搜索区域
        HBox searchRow = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("搜索客户姓名...");
        searchField.setPrefWidth(200);
        ComboBox<String> customerBox = new ComboBox<>();
        customerBox.setPromptText("选择客户");

        Runnable refreshCustomerBox = () -> {
            customerBox.getItems().clear();
            customerBox.getItems().add("无 - 全部");
            ctx.getElderlyDao().findAll().forEach(e ->
                customerBox.getItems().add(e.getId() + " - " + e.getName()));
        };
        refreshCustomerBox.run();
        customerBox.setValue("无 - 全部");

        searchField.textProperty().addListener((o, ov, nv) -> {
            customerBox.getItems().clear();
            customerBox.getItems().add("无 - 全部");
            List<Elderly> list = nv.trim().isEmpty() ? ctx.getElderlyDao().findAll()
                : ctx.getElderlyDao().findByName(nv.trim());
            list.forEach(e -> customerBox.getItems().add(e.getId() + " - " + e.getName()));
            customerBox.setValue("无 - 全部");
        });
        searchRow.getChildren().addAll(new Label("客户："), searchField, customerBox);

        // 已购项目表格
        TableView<CustomerCareProject> table = new TableView<>();
        TableColumn<CustomerCareProject, String> tc0 = new TableColumn<>("老人姓名");
        tc0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getCustomerId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getCustomerId());
        });
        TableColumn<CustomerCareProject, String> tc1 = tc("项目代码", "projectCode");
        TableColumn<CustomerCareProject, String> tc2 = tc("购买数量", "quantity");
        TableColumn<CustomerCareProject, String> tc3 = tc("购买日期", "purchaseDate");
        TableColumn<CustomerCareProject, String> tc4 = tc("到期日期", "expireDate");

        // 状态列 — 动态计算
        TableColumn<CustomerCareProject, String> tc5 = new TableColumn<>("状态");
        tc5.setCellValueFactory(data -> {
            CustomerCareProject c = data.getValue();
            String status;
            try {
                LocalDate expire = LocalDate.parse(c.getExpireDate() != null ? c.getExpireDate().substring(0, 10) : "2000-01-01");
                if (c.getQuantity() <= 0) status = "耗竭";
                else if (expire.isBefore(LocalDate.now())) status = "到期";
                else status = "正常";
            } catch (Exception e) { status = "未知"; }
            return new SimpleStringProperty(status);
        });
        table.getColumns().addAll(tc0, tc1, tc2, tc3, tc4, tc5);

        // 默认显示全部
        refreshTable(table, ctx.getCustomerCareProjectDao().findAll());

        // 双击行查看老人信息
        table.setRowFactory(tv -> {
            TableRow<CustomerCareProject> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    CustomerCareProject rowData = row.getItem();
                    Elderly e = ctx.getElderlyDao().findById(rowData.getCustomerId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        // 饮食偏好提示条
        HBox dietBar = new HBox(10);
        dietBar.setPadding(new Insets(8, 12, 8, 12));
        dietBar.setStyle("-fx-background-color:#fff3cd; -fx-background-radius:6; -fx-border-color:#ffc107; -fx-border-radius:6; -fx-border-width:1");
        dietBar.setVisible(false);
        dietBar.setManaged(false);

        // 选中客户时加载其服务项目 + 显示饮食偏好
        customerBox.setOnAction(e -> {
            String sel = customerBox.getValue();
            dietBar.getChildren().clear();
            dietBar.setVisible(false);
            dietBar.setManaged(false);
            if (sel == null) return;
            if ("无 - 全部".equals(sel)) {
                refreshTable(table, ctx.getCustomerCareProjectDao().findAll());
            } else {
                String customerId = sel.split(" - ")[0];
                refreshTable(table, ctx.getCustomerCareProjectDao().findByCustomerId(customerId));
                // 显示该客户的饮食偏好
                List<DietPreference> prefs = ctx.getDietPreferenceDao().findAll().stream()
                    .filter(p -> customerId.equals(p.getCustomerId())).toList();
                if (!prefs.isEmpty()) {
                    DietPreference dp = prefs.get(0);
                    java.util.List<String> tags = new java.util.ArrayList<>();
                    if (dp.getTaste() != null && !dp.getTaste().isEmpty()) tags.add("口味：" + dp.getTaste());
                    if (dp.getAllergies() != null && !dp.getAllergies().isEmpty()) tags.add("过敏原：" + dp.getAllergies());
                    if (dp.getTaboos() != null && !dp.getTaboos().isEmpty()) tags.add("忌口：" + dp.getTaboos());
                    if (dp.getDietaryAdvice() != null && !dp.getDietaryAdvice().isEmpty()) tags.add("建议：" + dp.getDietaryAdvice());
                    if (!tags.isEmpty()) {
                        Label dietLabel = new Label(" " + String.join("  |  ", tags));
                        dietLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#856404");
                        dietBar.getChildren().add(dietLabel);
                        dietBar.setVisible(true);
                        dietBar.setManaged(true);
                    }
                }
            }
        });

        // 操作按钮
        Button buyBtn = new Button("购买护理项目");
        Button renewBtn = new Button("续费");
        Button removeBtn = new Button("移除");

        buyBtn.setOnAction(e -> {
            String sel = customerBox.getValue();
            if (sel == null || "无 - 全部".equals(sel)) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择具体客户"); return; }
            String customerId = sel.split(" - ")[0];
            String customerName = sel.split(" - ")[1];
            // 检查是否有未购买的项目
            List<CustomerCareProject> owned = ctx.getCustomerCareProjectDao().findByCustomerId(customerId);
            List<CareProject> available = ctx.getCareProjectDao().findAll().stream()
                .filter(p -> owned.stream().noneMatch(o -> o.getProjectCode().equals(p.getCode())))
                .toList();
            if (available.isEmpty()) {
                LoginPane.showAlert(Alert.AlertType.INFORMATION, "该客户已购买所有护理项目，无需再买");
                return;
            }
            showBuyProjectDialog(customerId, customerName, table);
        });

        renewBtn.setOnAction(e -> {
            CustomerCareProject ccp = table.getSelectionModel().getSelectedItem();
            if (ccp == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要续费的项目"); return; }
            showRenewDialog(ccp, table);
        });

        removeBtn.setOnAction(e -> {
            CustomerCareProject ccp = table.getSelectionModel().getSelectedItem();
            if (ccp == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要移除的项目"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确定移除此护理项目吗？移除后客户将不再享有对应服务。", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    // 快照用于撤销
                    Map<String, Object> undoData = new HashMap<>();
                    undoData.put("type", "care_project_remove");
                    undoData.put("customerId", ccp.getCustomerId());
                    undoData.put("projectCode", ccp.getProjectCode());
                    undoData.put("quantity", ccp.getQuantity());
                    undoData.put("purchaseDate", ccp.getPurchaseDate());
                    undoData.put("expireDate", ccp.getExpireDate());
                    ctx.getCustomerCareProjectDao().delete(ccp.getId());
                    PersistentIdGenerator.getInstance().save();
                    AuditLogger.logReversible("移除护理项目", "服务关注", ccp.getProjectCode(), undoData);
                    String sel = customerBox.getValue();
                    if (sel == null) return;
                    if ("无 - 全部".equals(sel)) refreshTable(table, ctx.getCustomerCareProjectDao().findAll());
                    else refreshTable(table, ctx.getCustomerCareProjectDao().findByCustomerId(sel.split(" - ")[0]));
                }
            });
        });

        HBox btnRow = new HBox(10, buyBtn, renewBtn, removeBtn);
        box.getChildren().addAll(searchRow, dietBar, table, btnRow);
        return box;
    }

    private void showBuyProjectDialog(String customerId, String customerName, TableView<CustomerCareProject> table) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("为客户 " + customerName + " 购买护理项目");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        // 可用护理项目列表（客户未购买的）
        List<CustomerCareProject> owned = ctx.getCustomerCareProjectDao().findByCustomerId(customerId);
        List<CareProject> available = ctx.getCareProjectDao().findAll().stream()
            .filter(p -> owned.stream().noneMatch(o -> o.getProjectCode().equals(p.getCode())))
            .toList();

        ComboBox<String> projectBox = new ComboBox<>();
        available.forEach(p -> projectBox.getItems().add(p.getCode() + " - " + p.getName() + " (¥" + p.getPrice() + ")"));
        projectBox.setPromptText("选择护理项目");

        TextField qtyField = new TextField("1");
        DatePicker expirePicker = new DatePicker(LocalDate.now().plusMonths(3));

        grid.add(new Label("护理项目："), 0, 0); grid.add(projectBox, 1, 0);
        grid.add(new Label("数量："), 0, 1); grid.add(qtyField, 1, 1);
        grid.add(new Label("到期日期："), 0, 2); grid.add(expirePicker, 1, 2);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("购买", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == okBtn && projectBox.getValue() != null) {
                String projectCode = projectBox.getValue().split(" - ")[0];
                int qty;
                try { qty = Integer.parseInt(qtyField.getText().trim()); } catch (Exception ex) { qty = 1; }
                String expireDate = expirePicker.getValue() != null ? expirePicker.getValue().toString() : LocalDate.now().plusMonths(3).toString();
                String now = LocalDateTime.now().format(fmt);
                CustomerCareProject newCcp = new CustomerCareProject(null, customerId, projectCode, qty, now, expireDate);
                ctx.getCustomerCareProjectDao().insert(newCcp);
                PersistentIdGenerator.getInstance().save();
                Map<String, Object> undoData = new HashMap<>();
                undoData.put("type", "care_project_buy");
                undoData.put("projectId", newCcp.getId());
                AuditLogger.logReversible("购买护理项目", "服务关注", customerName + " ← " + projectCode, undoData);
                refreshTable(table, ctx.getCustomerCareProjectDao().findByCustomerId(customerId));
            }
            return null;
        });
        dlg.showAndWait();
    }

    private void showRenewDialog(CustomerCareProject ccp, TableView<CustomerCareProject> table) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("续费 " + ccp.getProjectCode());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        Label curQtyLabel = new Label("当前数量：" + ccp.getQuantity());
        TextField addQtyField = new TextField("1");
        Label curExpireLabel = new Label("当前到期：" + (ccp.getExpireDate() != null ? ccp.getExpireDate().substring(0, 10) : "无"));
        DatePicker newExpirePicker = new DatePicker(
            ccp.getExpireDate() != null && ccp.getExpireDate().length() >= 10
                ? LocalDate.parse(ccp.getExpireDate().substring(0, 10)).plusMonths(3)
                : LocalDate.now().plusMonths(3));

        grid.add(curQtyLabel, 0, 0);
        grid.add(new Label("新增数量："), 0, 1); grid.add(addQtyField, 1, 1);
        grid.add(curExpireLabel, 0, 2);
        grid.add(new Label("新到期日期："), 0, 3); grid.add(newExpirePicker, 1, 3);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("续费", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == okBtn) {
                int oldQty = ccp.getQuantity();
                String oldExpireDate = ccp.getExpireDate();
                int addQty;
                try { addQty = Integer.parseInt(addQtyField.getText().trim()); } catch (Exception ex) { addQty = 0; }
                ccp.setQuantity(oldQty + addQty);
                if (newExpirePicker.getValue() != null) ccp.setExpireDate(newExpirePicker.getValue().toString());
                ctx.getCustomerCareProjectDao().update(ccp);
                PersistentIdGenerator.getInstance().save();
                Map<String, Object> undoData = new HashMap<>();
                undoData.put("type", "care_project_renew");
                undoData.put("projectId", ccp.getId());
                undoData.put("oldQuantity", oldQty);
                undoData.put("oldExpireDate", oldExpireDate != null ? oldExpireDate : "");
                AuditLogger.logReversible("续费护理项目", "服务关注", ccp.getProjectCode(), undoData);
                refreshTable(table, ctx.getCustomerCareProjectDao().findByCustomerId(ccp.getCustomerId()));
            }
            return null;
        });
        dlg.showAndWait();
    }

    // ==================== 管家分配 ====================
    private VBox buildButlerAssign() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        HBox topRow = new HBox(10);
        ComboBox<String> butlerBox = new ComboBox<>();
        butlerBox.setPromptText("选择健康管家(护工)");
        butlerBox.getItems().add("无 - 全部");
        java.util.List<Employee> allEmps = ctx.getEmployeeDao().findAll();
        for (Employee emp : allEmps) {
            if (emp.getPosition() != null && (emp.getPosition().contains("管家") || emp.getPosition().contains("护工"))) {
                butlerBox.getItems().add(emp.getEmployeeId() + " - " + emp.getName());
            }
        }
        butlerBox.setValue("无 - 全部");

        topRow.getChildren().addAll(new Label("健康管家："), butlerBox);

        // 服务客户列表 — 用自定义列解析名称
        TableView<ServiceAssignment> table = new TableView<>();
        TableColumn<ServiceAssignment, String> c0 = new TableColumn<>("老人姓名");
        c0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getElderlyId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getElderlyId());
        });
        TableColumn<ServiceAssignment, String> c1 = tc("服务类型", "serviceType");
        TableColumn<ServiceAssignment, String> c2 = tc("开始日期", "startDate");
        TableColumn<ServiceAssignment, String> c3 = tc("状态", "status");
        table.getColumns().addAll(c0, c1, c2, c3);

        // 默认显示全部
        refreshTable(table, ctx.getServiceAssignmentDao().findAll());

        table.setRowFactory(tv -> {
            TableRow<ServiceAssignment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Elderly e = ctx.getElderlyDao().findById(row.getItem().getElderlyId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        butlerBox.setOnAction(e -> {
            String sel = butlerBox.getValue();
            if (sel == null) return;
            if ("无 - 全部".equals(sel)) {
                refreshTable(table, ctx.getServiceAssignmentDao().findAll());
            } else {
                String empId = sel.split(" - ")[0];
                refreshTable(table, ctx.getServiceAssignmentDao().findByEmployeeId(empId));
            }
        });

        Button addClientBtn = new Button("添加服务客户");
        Button removeClientBtn = new Button("移除服务客户");

        addClientBtn.setOnAction(e -> {
            String sel = butlerBox.getValue();
            if (sel == null || "无 - 全部".equals(sel)) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择具体管家"); return; }
            String empId = sel.split(" - ")[0];
            String empName = sel.split(" - ")[1];

            // 查询无管家客户（只查在住老人）
            List<Elderly> allElders = ctx.getElderlyDao().findAll().stream()
                .filter(el -> "在住".equals(el.getStatus())).toList();
            List<ServiceAssignment> allAssigns = ctx.getServiceAssignmentDao().findAll();
            List<Elderly> unassigned = allElders.stream()
                .filter(el -> allAssigns.stream().noneMatch(a -> a.getElderlyId().equals(el.getId())))
                .toList();

            if (unassigned.isEmpty()) { LoginPane.showAlert(Alert.AlertType.INFORMATION, "没有未分配管家的在住客户"); return; }

            ChoiceDialog<String> dlg = new ChoiceDialog<>(
                unassigned.get(0).getId() + " - " + unassigned.get(0).getName(),
                unassigned.stream().map(el -> el.getId() + " - " + el.getName()).toList());
            dlg.initOwner(stage);
            dlg.setTitle("选择客户");
            dlg.setHeaderText("为管家 " + empName + " 添加服务客户");
            dlg.showAndWait().ifPresent(clientSel -> {
                String elderId = clientSel.split(" - ")[0];
                ServiceAssignment sa = new ServiceAssignment();
                sa.setEmployeeId(empId);
                sa.setElderlyId(elderId);
                sa.setServiceType("日常护理");
                sa.setStartDate(LocalDate.now().toString());
                sa.setStatus("服务中");
                ctx.getServiceAssignmentDao().insert(sa);
                PersistentIdGenerator.getInstance().save();
                refreshTable(table, ctx.getServiceAssignmentDao().findByEmployeeId(empId));
                Map<String, Object> undoData = new HashMap<>();
                undoData.put("type", "service_assign");
                undoData.put("assignmentId", sa.getAssignmentId());
                AuditLogger.logReversible("设置服务对象", "管家分配", empName + " ← " + clientSel, undoData);
            });
        });

        removeClientBtn.setOnAction(e -> {
            ServiceAssignment sa = table.getSelectionModel().getSelectedItem();
            if (sa == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要移除的客户"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确定移除此服务客户吗？移除操作不会影响护理记录。", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    ctx.getServiceAssignmentDao().delete(sa.getAssignmentId());
                    PersistentIdGenerator.getInstance().save();
                    String selValue = butlerBox.getValue();
                    if (selValue == null) return;
                    if ("无 - 全部".equals(selValue)) refreshTable(table, ctx.getServiceAssignmentDao().findAll());
                    else refreshTable(table, ctx.getServiceAssignmentDao().findByEmployeeId(selValue.split(" - ")[0]));
                }
            });
        });

        HBox btnRow = new HBox(10, addClientBtn, removeClientBtn);
        box.getChildren().addAll(topRow, table, btnRow);
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
            confirm.initOwner(stage);
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
        dlg.initOwner(stage);
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
            confirm.initOwner(stage);
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
        dlg.initOwner(stage);
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
        Label title = new Label("外出登记列表");
        TableView<OutRegistration> table = new TableView<>();
        TableColumn<OutRegistration, String> c1 = tc("老人ID", "customerId");
        TableColumn<OutRegistration, String> c2 = tc("外出时间", "outTime");
        TableColumn<OutRegistration, String> c3 = tc("预计归时", "expectedReturnTime");
        TableColumn<OutRegistration, String> c4 = tc("实际归时", "actualReturnTime");
        TableColumn<OutRegistration, String> c5 = tc("审批状态", "approvalStatus");
        TableColumn<OutRegistration, String> c6 = tc("审批人", "approver");
        TableColumn<OutRegistration, String> c7 = tc("审批时间", "approvalTime");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);

        Runnable refresh = () -> refreshTable(table, ctx.getOutRegistrationDao().findAll());
        refresh.run();

        table.setRowFactory(tv -> {
            TableRow<OutRegistration> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Elderly e = ctx.getElderlyDao().findById(row.getItem().getCustomerId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        Button approveBtn = new Button("审批通过");
        Button rejectBtn = new Button("审批不通过");

        approveBtn.setOnAction(e -> {
            OutRegistration r = table.getSelectionModel().getSelectedItem();
            if (r == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            r.setApprovalStatus("通过");
            r.setApprovalTime(LocalDateTime.now().format(fmt));
            r.setApprover(user.getRealName());
            ctx.getOutRegistrationDao().update(r);
            // 修改床位状态为外出
            Elderly elder = ctx.getElderlyDao().findById(r.getCustomerId());
            if (elder != null && elder.getBedId() != null) {
                Bed bed = ctx.getBedDao().findById(elder.getBedId());
                if (bed != null) { bed.setStatus("out"); ctx.getBedDao().update(bed); }
            }
            PersistentIdGenerator.getInstance().save();
            refresh.run();
        });

        rejectBtn.setOnAction(e -> {
            OutRegistration r = table.getSelectionModel().getSelectedItem();
            if (r == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            r.setApprovalStatus("不通过");
            r.setApprovalTime(LocalDateTime.now().format(fmt));
            r.setApprover(user.getRealName());
            ctx.getOutRegistrationDao().update(r);
            PersistentIdGenerator.getInstance().save();
            refresh.run();
        });

        Button returnBtn = new Button("登记归来");
        returnBtn.setOnAction(e -> {
            OutRegistration r = table.getSelectionModel().getSelectedItem();
            if (r == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            if (!"通过".equals(r.getApprovalStatus())) { LoginPane.showAlert(Alert.AlertType.WARNING, "该申请尚未审批通过"); return; }
            if ("已归来".equals(r.getStatus())) { LoginPane.showAlert(Alert.AlertType.WARNING, "该老人已归来"); return; }
            r.setActualReturnTime(LocalDateTime.now().format(fmt));
            r.setStatus("已归来");
            ctx.getOutRegistrationDao().update(r);
            // 恢复床位状态
            Elderly elder = ctx.getElderlyDao().findById(r.getCustomerId());
            if (elder != null && elder.getBedId() != null) {
                Bed bed = ctx.getBedDao().findById(elder.getBedId());
                if (bed != null) { bed.setStatus("occupied"); ctx.getBedDao().update(bed); }
            }
            PersistentIdGenerator.getInstance().save();
            refresh.run();
        });

        HBox btns = new HBox(10, approveBtn, rejectBtn, returnBtn);
        box.getChildren().addAll(title, btns, table);
        return box;
    }

    // ==================== 退住管理 ====================
    private VBox buildCheckoutManage() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        Label title = new Label("退住登记列表");
        TableView<CheckOut> table = new TableView<>();
        TableColumn<CheckOut, String> c1 = tc("老人ID", "customerId");
        TableColumn<CheckOut, String> c2 = tc("退住类型", "checkoutType");
        TableColumn<CheckOut, String> c3 = tc("退住日期", "checkoutDate");
        TableColumn<CheckOut, String> c4 = tc("原因", "reason");
        TableColumn<CheckOut, String> c5 = tc("审批状态", "approvalStatus");
        TableColumn<CheckOut, String> c6 = tc("审批人", "approver");
        TableColumn<CheckOut, String> c7 = tc("审批时间", "approvalTime");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);

        Runnable refresh = () -> refreshTable(table, ctx.getCheckOutDao().findAll());
        refresh.run();

        table.setRowFactory(tv -> {
            TableRow<CheckOut> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Elderly e = ctx.getElderlyDao().findById(row.getItem().getCustomerId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        Button approveBtn = new Button("审批通过");
        Button rejectBtn = new Button("审批不通过");

        approveBtn.setOnAction(e -> {
            CheckOut co = table.getSelectionModel().getSelectedItem();
            if (co == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            co.setApprovalStatus("通过");
            co.setApprovalTime(LocalDateTime.now().format(fmt));
            co.setApprover(user.getRealName());
            ctx.getCheckOutDao().update(co);
            // 正常退住或死亡退住：修改床位为空闲
            if ("正常退住".equals(co.getCheckoutType()) || "死亡退住".equals(co.getCheckoutType())) {
                Elderly elder = ctx.getElderlyDao().findById(co.getCustomerId());
                if (elder != null) {
                    elder.setStatus("退住");
                    ctx.getElderlyDao().update(elder);
                    if (elder.getBedId() != null) {
                        Bed bed = ctx.getBedDao().findById(elder.getBedId());
                        if (bed != null) { bed.setStatus("available"); ctx.getBedDao().update(bed); }
                    }
                }
            }
            PersistentIdGenerator.getInstance().save();
            refresh.run();
        });

        rejectBtn.setOnAction(e -> {
            CheckOut co = table.getSelectionModel().getSelectedItem();
            if (co == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            co.setApprovalStatus("不通过");
            co.setApprovalTime(LocalDateTime.now().format(fmt));
            co.setApprover(user.getRealName());
            ctx.getCheckOutDao().update(co);
            PersistentIdGenerator.getInstance().save();
            refresh.run();
        });

        HBox btns = new HBox(10, approveBtn, rejectBtn);
        box.getChildren().addAll(title, btns, table);
        return box;
    }

    // ==================== 健康记录 ====================
    private VBox buildHealthRecords() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        HBox topRow = new HBox(10);
        ComboBox<String> customerBox = new ComboBox<>();
        customerBox.setPromptText("选择老人");
        customerBox.getItems().add("无 - 全部");
        ctx.getElderlyDao().findAll().forEach(e ->
            customerBox.getItems().add(e.getId() + " - " + e.getName()));
        customerBox.setValue("无 - 全部");

        topRow.getChildren().addAll(new Label("老人："), customerBox);

        TableView<HealthRecord> table = new TableView<>();
        TableColumn<HealthRecord, String> c0 = new TableColumn<>("老人姓名");
        c0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getCustomerId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getCustomerId());
        });
        TableColumn<HealthRecord, String> c1 = tc("记录日期", "recordDate");
        TableColumn<HealthRecord, String> c2 = tc("血压", "bloodPressure");
        TableColumn<HealthRecord, String> c3 = tc("心率", "heartRate");
        TableColumn<HealthRecord, String> c4 = tc("血糖", "bloodSugar");
        TableColumn<HealthRecord, String> c5 = tc("体重(kg)", "weight");
        TableColumn<HealthRecord, String> c6 = tc("体温(℃)", "temperature");
        TableColumn<HealthRecord, String> c7 = tc("备注", "remark");
        table.getColumns().addAll(c0, c1, c2, c3, c4, c5, c6, c7);
        refreshTable(table, ctx.getHealthRecordDao().findAll());

        table.setRowFactory(tv -> {
            TableRow<HealthRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Elderly e = ctx.getElderlyDao().findById(row.getItem().getCustomerId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        customerBox.setOnAction(e -> {
            String sel = customerBox.getValue();
            if (sel == null) return;
            if ("无 - 全部".equals(sel)) refreshTable(table, ctx.getHealthRecordDao().findAll());
            else refreshTable(table, ctx.getHealthRecordDao().findByCustomerId(sel.split(" - ")[0]));
        });

        Button addBtn = new Button("登记健康记录");
        Button delBtn = new Button("删除记录");

        addBtn.setOnAction(e -> {
            Dialog<HealthRecord> dlg = new Dialog<>();
            dlg.initOwner(stage);
            dlg.setTitle("登记健康记录");
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

            ComboBox<String> elderBox = new ComboBox<>();
            elderBox.setPromptText("选择老人");
            ctx.getElderlyDao().findAll().forEach(el ->
                elderBox.getItems().add(el.getId() + " - " + el.getName()));
            if (elderBox.getItems().isEmpty()) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "请先在老人管理中录入老人信息");
                return;
            }
            elderBox.setValue(elderBox.getItems().get(0));

            DatePicker datePick = new DatePicker(LocalDate.now());
            TextField bpField = new TextField();
            bpField.setPromptText("如 120/80");
            TextField hrField = new TextField();
            hrField.setPromptText("次/分钟");
            TextField bsField = new TextField();
            bsField.setPromptText("mmol/L");
            TextField weightField = new TextField();
            weightField.setPromptText("kg");
            TextField tempField = new TextField();
            tempField.setPromptText("℃");
            TextField remarkField = new TextField();

            grid.add(new Label("老人："), 0, 0); grid.add(elderBox, 1, 0);
            grid.add(new Label("日期："), 0, 1); grid.add(datePick, 1, 1);
            grid.add(new Label("血压："), 0, 2); grid.add(bpField, 1, 2);
            grid.add(new Label("心率："), 0, 3); grid.add(hrField, 1, 3);
            grid.add(new Label("血糖："), 0, 4); grid.add(bsField, 1, 4);
            grid.add(new Label("体重："), 0, 5); grid.add(weightField, 1, 5);
            grid.add(new Label("体温："), 0, 6); grid.add(tempField, 1, 6);
            grid.add(new Label("备注："), 0, 7); grid.add(remarkField, 1, 7);
            dlg.getDialogPane().setContent(grid);

            dlg.setResultConverter(bt -> {
                if (bt == ButtonType.OK) {
                    HealthRecord hr = new HealthRecord();
                    hr.setCustomerId(elderBox.getValue().split(" - ")[0]);
                    hr.setRecordDate(datePick.getValue().toString());
                    hr.setBloodPressure(bpField.getText().trim());
                    hr.setHeartRate(hrField.getText().trim());
                    hr.setBloodSugar(bsField.getText().trim());
                    hr.setWeight(weightField.getText().trim());
                    hr.setTemperature(tempField.getText().trim());
                    hr.setRemark(remarkField.getText().trim());
                    return hr;
                }
                return null;
            });

            dlg.showAndWait().ifPresent(hr -> {
                ctx.getHealthRecordDao().insert(hr);
                PersistentIdGenerator.getInstance().save();
                String sel = customerBox.getValue();
                if (sel == null || "无 - 全部".equals(sel)) refreshTable(table, ctx.getHealthRecordDao().findAll());
                else refreshTable(table, ctx.getHealthRecordDao().findByCustomerId(sel.split(" - ")[0]));
                Map<String, Object> undoData = new HashMap<>();
                undoData.put("type", "health_record");
                undoData.put("recordId", hr.getHealthId());
                AuditLogger.logReversible("登记健康记录", "健康记录", elderBox.getValue(), undoData);
            });
        });

        delBtn.setOnAction(e -> {
            HealthRecord sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要删除的记录"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确定删除此健康记录吗？", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    ctx.getHealthRecordDao().delete(sel.getHealthId());
                    PersistentIdGenerator.getInstance().save();
                    String selValue = customerBox.getValue();
                    if (selValue == null || "无 - 全部".equals(selValue)) refreshTable(table, ctx.getHealthRecordDao().findAll());
                    else refreshTable(table, ctx.getHealthRecordDao().findByCustomerId(selValue.split(" - ")[0]));
                }
            });
        });

        HBox btnRow = new HBox(10, addBtn, delBtn);
        box.getChildren().addAll(topRow, table, btnRow);
        return box;
    }

    // ==================== 操作日志 ====================
    private VBox buildLogView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<OperationLog> table = new TableView<>();
        TableColumn<OperationLog, String> c1 = tc("操作人", "operatorName");
        TableColumn<OperationLog, String> c2 = tc("角色", "operatorRole");
        TableColumn<OperationLog, String> c3 = tc("操作", "action");
        TableColumn<OperationLog, String> c4 = tc("目标", "target");
        TableColumn<OperationLog, String> c5 = tc("详情", "detail");
        TableColumn<OperationLog, String> c6 = tc("时间", "time");
        TableColumn<OperationLog, String> c7 = new TableColumn<>("状态");
        c7.setCellValueFactory(data -> {
            OperationLog log = data.getValue();
            String text;
            if (log.getReversibleData() == null || log.getReversibleData().isEmpty()) {
                text = "不可撤销";
            } else if (log.isReverted()) {
                text = "已撤销";
            } else {
                text = "可撤销";
            }
            return new SimpleStringProperty(text);
        });
        TableColumn<OperationLog, Void> c8 = new TableColumn<>("跳转");
        c8.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("→");
            {
                btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    OperationLog log = getTableView().getItems().get(getIndex());
                    String key = targetToModule(log.getTarget());
                    if (key != null) switchContent(key);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8);

        List<OperationLog> logs = ctx.getOperationLogDao().findAll();
        logs.sort((a, b) -> {
            String ta = a.getTime() != null ? a.getTime() : "";
            String tb = b.getTime() != null ? b.getTime() : "";
            return tb.compareTo(ta); // 最新在前
        });
        refreshTable(table, logs);

        Button undoBtn = new Button("撤销选中操作");
        undoBtn.setOnAction(e -> {
            OperationLog sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择一条操作日志"); return; }
            if (sel.getReversibleData() == null || sel.getReversibleData().isEmpty()) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "此操作不支持撤销");
                return;
            }
            if (sel.isReverted()) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "此操作已被撤销过");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "确定要撤销操作 \"" + sel.getAction() + " - " + sel.getDetail() + "\" 吗？");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    boolean success = performUndo(sel);
                    if (success) {
                        sel.setReverted(true);
                        ctx.getOperationLogDao().update(sel);
                        PersistentIdGenerator.getInstance().save();
                        AuditLogger.log("撤销操作", "操作日志", sel.getAction() + " - " + sel.getDetail());
                        List<OperationLog> refreshed = ctx.getOperationLogDao().findAll();
                        refreshed.sort((a, b) -> {
                            String ta = a.getTime() != null ? a.getTime() : "";
                            String tb = b.getTime() != null ? b.getTime() : "";
                            return tb.compareTo(ta);
                        });
                        refreshTable(table, refreshed);
                    }
                }
            });
        });

        HBox btnRow = new HBox(10, undoBtn);
        box.getChildren().addAll(btnRow, table);
        return box;
    }

    /** 操作日志目标 → 左侧导航模块映射 */
    private String targetToModule(String target) {
        if (target == null) return null;
        if (target.contains("老人")) return "elderly";
        if (target.contains("床位")) return "beds";
        if (target.contains("护理级别") || target.contains("护理项目") || target.contains("护理记录")) return "nlevels";
        if (target.contains("服务关注") || target.contains("服务")) return "serviceFocus";
        if (target.contains("管家")) return "butlerAssign";
        if (target.contains("健康")) return "health";
        if (target.contains("膳食") || target.contains("餐饮")) return "foods";
        if (target.contains("用户")) return "users";
        if (target.contains("员工")) return "employees";
        if (target.contains("外出")) return "outreg";
        if (target.contains("退住")) return "checkout";
        if (target.contains("日志")) return "logs";
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean performUndo(OperationLog log) {
        Map<String, Object> data = AuditLogger.parseReversibleData(log.getReversibleData());
        if (data == null) return false;
        String type = (String) data.get("type");
        try {
            switch (type) {
                case "elderly_edit": {
                    String elderlyId = (String) data.get("elderlyId");
                    Elderly e = ctx.getElderlyDao().findById(elderlyId);
                    if (e == null) return false;
                    e.setName((String) data.get("oldName"));
                    e.setAge(data.get("oldAge") instanceof Integer ? (int) data.get("oldAge") : Integer.parseInt(String.valueOf(data.get("oldAge"))));
                    e.setGender((String) data.get("oldGender"));
                    e.setIdCard((String) data.get("oldIdCard"));
                    e.setBloodType((String) data.get("oldBloodType"));
                    e.setBirthDate((String) data.get("oldBirthDate"));
                    e.setPhone((String) data.get("oldPhone"));
                    e.setAddress((String) data.get("oldAddress"));
                    e.setFamilyMember((String) data.get("oldFamilyMember"));
                    e.setEmergencyContact((String) data.get("oldEmergencyContact"));
                    e.setEmergencyPhone((String) data.get("oldEmergencyPhone"));
                    // 恢复床位
                    String oldBedId = (String) data.get("oldBedId");
                    String curBedId = e.getBedId();
                    if (oldBedId != null && !oldBedId.equals(curBedId)) {
                        if (curBedId != null && !curBedId.isEmpty()) {
                            Bed curBed = ctx.getBedDao().findById(curBedId);
                            if (curBed != null) { curBed.setStatus("available"); ctx.getBedDao().update(curBed); }
                        }
                        if (!oldBedId.isEmpty()) {
                            Bed oldBed = ctx.getBedDao().findById(oldBedId);
                            if (oldBed != null) { oldBed.setStatus("occupied"); ctx.getBedDao().update(oldBed); }
                        }
                        e.setBedId(oldBedId.isEmpty() ? null : oldBedId);
                    }
                    e.setCheckInDate((String) data.get("oldCheckInDate"));
                    e.setContractEndDate((String) data.get("oldContractEndDate"));
                    e.setRoomNo((String) data.get("oldRoomNo"));
                    e.setBuildingId((String) data.get("oldBuildingId"));
                    ctx.getElderlyDao().update(e);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "nursing_level_set": {
                    String elderlyId = (String) data.get("elderlyId");
                    Elderly e = ctx.getElderlyDao().findById(elderlyId);
                    if (e == null) return false;
                    // 删除添加的护理项目
                    List<String> addedIds = (List<String>) data.get("addedProjectIds");
                    if (addedIds != null) {
                        for (String id : addedIds) ctx.getCustomerCareProjectDao().delete(id);
                    }
                    // 恢复旧级别
                    String oldLevelCode = (String) data.get("oldLevelCode");
                    e.setNursingLevelCode(oldLevelCode.isEmpty() ? null : oldLevelCode);
                    ctx.getElderlyDao().update(e);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "nursing_level_remove": {
                    String elderlyId = (String) data.get("elderlyId");
                    Elderly e = ctx.getElderlyDao().findById(elderlyId);
                    if (e == null) return false;
                    String oldLevelCode = (String) data.get("oldLevelCode");
                    e.setNursingLevelCode(oldLevelCode.isEmpty() ? null : oldLevelCode);
                    ctx.getElderlyDao().update(e);
                    // 恢复已购护理项目
                    List<Map<String, String>> removedProjects = (List<Map<String, String>>) data.get("removedProjects");
                    if (removedProjects != null) {
                        for (Map<String, String> info : removedProjects) {
                            CustomerCareProject ccp = new CustomerCareProject(
                                null, elderlyId,
                                info.get("projectCode"),
                                Integer.parseInt(info.get("quantity")),
                                info.get("purchaseDate"),
                                info.get("expireDate"));
                            ctx.getCustomerCareProjectDao().insert(ccp);
                        }
                    }
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "bed_swap": {
                    String elderlyId = (String) data.get("elderlyId");
                    Elderly e = ctx.getElderlyDao().findById(elderlyId);
                    if (e == null) return false;
                    String oldBedId = (String) data.get("oldBedId");
                    String newBedId = (String) data.get("newBedId");
                    // 换回去：当前占用的床位释放，旧床位恢复占用
                    if (newBedId != null && !newBedId.isEmpty()) {
                        Bed newBed = ctx.getBedDao().findById(newBedId);
                        if (newBed != null) { newBed.setStatus("available"); ctx.getBedDao().update(newBed); }
                    }
                    if (oldBedId != null && !oldBedId.isEmpty()) {
                        Bed oldBed = ctx.getBedDao().findById(oldBedId);
                        if (oldBed != null) { oldBed.setStatus("occupied"); ctx.getBedDao().update(oldBed); }
                    }
                    e.setBedId(oldBedId.isEmpty() ? null : oldBedId);
                    e.setRoomNo((String) data.get("oldRoomNo"));
                    e.setBuildingId((String) data.get("oldBuildingId"));
                    ctx.getElderlyDao().update(e);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "service_assign": {
                    String assignmentId = (String) data.get("assignmentId");
                    ctx.getServiceAssignmentDao().delete(assignmentId);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "health_record": {
                    String recordId = (String) data.get("recordId");
                    ctx.getHealthRecordDao().delete(recordId);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "diet_preference": {
                    String preferenceId = (String) data.get("preferenceId");
                    ctx.getDietPreferenceDao().delete(preferenceId);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "elderly_checkin": {
                    String elderlyId = (String) data.get("elderlyId");
                    String bedId = (String) data.get("bedId");
                    ctx.getElderlyDao().delete(elderlyId);
                    if (bedId != null && !bedId.isEmpty()) {
                        Bed bed = ctx.getBedDao().findById(bedId);
                        if (bed != null) { bed.setStatus("available"); ctx.getBedDao().update(bed); }
                    }
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "care_project_buy": {
                    String projectId = (String) data.get("projectId");
                    ctx.getCustomerCareProjectDao().delete(projectId);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "care_project_renew": {
                    String projectId = (String) data.get("projectId");
                    CustomerCareProject ccp = ctx.getCustomerCareProjectDao().findById(projectId);
                    if (ccp == null) return false;
                    int oldQty = data.get("oldQuantity") instanceof Integer ? (int) data.get("oldQuantity") : Integer.parseInt(String.valueOf(data.get("oldQuantity")));
                    ccp.setQuantity(oldQty);
                    ccp.setExpireDate((String) data.get("oldExpireDate"));
                    ctx.getCustomerCareProjectDao().update(ccp);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                case "care_project_remove": {
                    String customerId = (String) data.get("customerId");
                    String projectCode = (String) data.get("projectCode");
                    int qty = data.get("quantity") instanceof Integer ? (int) data.get("quantity") : Integer.parseInt(String.valueOf(data.get("quantity")));
                    String purchaseDate = (String) data.get("purchaseDate");
                    String expireDate = (String) data.get("expireDate");
                    CustomerCareProject ccp = new CustomerCareProject(null, customerId, projectCode, qty, purchaseDate, expireDate);
                    ctx.getCustomerCareProjectDao().insert(ccp);
                    PersistentIdGenerator.getInstance().save();
                    return true;
                }
                default:
                    return false;
            }
        } catch (Exception ex) {
            LoginPane.showAlert(Alert.AlertType.ERROR, "撤销失败: " + ex.getMessage());
            return false;
        }
    }

    // ==================== 辅助方法 ====================

    /** 校验18位身份证号，合法返回出生日期字符串(YYYY-MM-DD)，否则返回null */
    private String validateIdCard(String id) {
        if (id == null || id.length() != 18) return null;
        for (int i = 0; i < 17; i++) {
            if (!Character.isDigit(id.charAt(i))) return null;
        }
        char last = id.charAt(17);
        if (!Character.isDigit(last) && last != 'X' && last != 'x') return null;

        // 提取出生日期
        String birth = id.substring(6, 14);
        try {
            LocalDate.parse(birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8));
        } catch (Exception e) {
            return null;
        }

        // 校验码
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (id.charAt(i) - '0') * weights[i];
        }
        char expected = checkCodes[sum % 11];
        if (expected == 'X' && (last == 'X' || last == 'x')) return birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
        if (Character.toUpperCase(last) != expected) return null;

        return birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
    }

    private <T> TableColumn<T, String> tc(String title, String property) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(120);
        return col;
    }

    private <T> void refreshTable(TableView<T> table, List<T> data) {
        table.setItems(FXCollections.observableArrayList(data));
        table.refresh();
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
        TableColumn<Elderly, String> c4 = tc("血型", "bloodType");
        TableColumn<Elderly, String> c5 = tc("身份证", "idCard");
        TableColumn<Elderly, String> c6 = tc("电话", "phone");
        TableColumn<Elderly, String> c7 = tc("家属", "familyMember");
        TableColumn<Elderly, String> c8 = tc("楼栋", "buildingId");
        TableColumn<Elderly, String> c9 = tc("房间号", "roomNo");
        TableColumn<Elderly, String> c10 = tc("床位ID", "bedId");
        TableColumn<Elderly, String> c11 = tc("护理等级", "nursingLevelCode");
        TableColumn<Elderly, String> c12 = tc("入住日期", "checkInDate");
        TableColumn<Elderly, String> c13 = tc("合同到期", "contractEndDate");
        TableColumn<Elderly, String> c14 = tc("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14);
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
