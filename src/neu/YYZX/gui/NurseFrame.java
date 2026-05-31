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
import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.model.*;
import neu.YYZX.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NurseFrame {

    private final User user;
    private final String token;
    private final DataInitializer ctx = DataInitializer.getInstance();
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, ToggleButton> navButtons = new LinkedHashMap<>();
    private final Label moduleTitle = new Label();

    public NurseFrame(User user, String token) {
        this.user = user;
        this.token = token;
        this.stage = new Stage();
        stage.setTitle("东软颐养中心 - 护工端 [" + (user.getRealName() != null ? user.getRealName() : user.getUsername()) + "]");
        buildUI();
    }

    public void show() {
        stage.setScene(new Scene(root, 1000, 680));
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
        VBox nav = new VBox(3);
        nav.setPadding(new Insets(10));
        nav.setPrefWidth(145);
        nav.setStyle("-fx-background-color:#2c3e50");

        String[][] items = {
            {"🧓 老人信息", "elderly"}, {"📝 护理记录", "records"}, {"📋 服务管理", "services"},
            {"🍽 膳食偏好", "diet"}, {"🚶 外出申请", "outreg"}, {"🏠 退住申请", "checkout"},
            {"💬 我的消息", "messages"}
        };

        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < items.length; i++) {
            ToggleButton btn = new ToggleButton(items[i][0]);
            btn.setToggleGroup(group);
            btn.setPrefWidth(125);
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

        VBox contentWrapper = new VBox(10);
        contentWrapper.setPadding(new Insets(15, 20, 15, 20));
        contentWrapper.getChildren().addAll(moduleTitle, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(8, 15, 8, 15));
        topBar.setStyle("-fx-background-color:#ecf0f1");
        topBar.setAlignment(Pos.CENTER_RIGHT);
        String displayName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        Label userLabel = new Label("当前用户：" + displayName + "（" + user.getUsername() + "）");

        int unread = 0;
        for (Message m : ctx.getMessageDao().findAll()) {
            if (displayName.equals(m.getReceiverName()) && !m.isRead()) unread++;
        }
        if (unread > 0) userLabel.setText(userLabel.getText() + " | 未读消息：" + unread + " 条");

        Button logoutBtn = new Button("退出登录");
        logoutBtn.setOnAction(e -> {
            stage.close();
            Stage loginStage = new Stage();
            LoginPane loginPane = new LoginPane(loginStage);
            loginStage.setScene(new Scene(loginPane, 450, 420));
            loginStage.setTitle("东软颐养中心管理系统");
            loginStage.setResizable(false);
            loginStage.show();
        });
        topBar.getChildren().addAll(userLabel, logoutBtn);

        root.setTop(topBar);
        root.setLeft(nav);
        root.setCenter(contentWrapper);
        switchContent("elderly");
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

    private Map<String, String> moduleNames = Map.of(
        "elderly", "老人信息", "records", "护理记录", "services", "服务管理",
        "diet", "膳食偏好", "outreg", "外出申请", "checkout", "退住申请",
        "messages", "我的消息"
    );

    private void switchContent(String key) {
        updateNavStyles(key);
        moduleTitle.setText("▸ " + moduleNames.getOrDefault(key, key));

        javafx.scene.Node content;
        switch (key) {
            case "elderly": content = buildElderlyInfo(); break;
            case "records": content = buildCareRecords(); break;
            case "services": content = buildServices(); break;
            case "diet": content = buildDiet(); break;
            case "outreg": content = buildOutRegApply(); break;
            case "checkout": content = buildCheckoutApply(); break;
            case "messages": content = buildMessages(); break;
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

    // ==================== 老人信息 ====================
    private VBox buildElderlyInfo() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TextField searchField = new TextField();
        searchField.setPromptText("搜索老人姓名...");
        searchField.setPrefWidth(200);

        TableView<Elderly> table = new TableView<>();
        TableColumn<Elderly, String> c1 = col("姓名", "name");
        TableColumn<Elderly, String> c2 = col("年龄", "age");
        TableColumn<Elderly, String> c3 = col("性别", "gender");
        TableColumn<Elderly, String> c4 = col("血型", "bloodType");
        TableColumn<Elderly, String> c5 = col("身份证", "idCard");
        TableColumn<Elderly, String> c6 = col("电话", "phone");
        TableColumn<Elderly, String> c7 = col("家属", "familyMember");
        TableColumn<Elderly, String> c8 = col("楼栋", "buildingId");
        TableColumn<Elderly, String> c9 = col("房间号", "roomNo");
        TableColumn<Elderly, String> c10 = col("床位ID", "bedId");
        TableColumn<Elderly, String> c11 = col("护理等级", "nursingLevelCode");
        TableColumn<Elderly, String> c12 = col("合同到期", "contractEndDate");
        TableColumn<Elderly, String> c13 = col("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13);
        refresh(table, ctx.getElderlyDao().findAll());

        searchField.textProperty().addListener((o, ov, nv) -> {
            if (nv.trim().isEmpty()) refresh(table, ctx.getElderlyDao().findAll());
            else refresh(table, ctx.getElderlyDao().findByName(nv.trim()));
        });

        Button checkinBtn = new Button("老人入住");
        checkinBtn.setOnAction(e -> {
            long availBeds = ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus())).count();
            if (availBeds == 0) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "没有空闲床位，请先由管理员创建房间和床位");
                return;
            }
            showCheckinDialog(table);
        });

        HBox topRow = new HBox(10, searchField, checkinBtn);
        box.getChildren().addAll(topRow, table);
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
        ComboBox<String> bloodType = new ComboBox<>();
        bloodType.getItems().addAll("A", "B", "AB", "O"); bloodType.setValue("A");
        DatePicker birthDate = new DatePicker();
        TextField phone = new TextField();
        TextField familyMember = new TextField();
        TextField emContact = new TextField();
        TextField emPhone = new TextField();
        ComboBox<String> bedBox = new ComboBox<>();
        ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus()))
            .forEach(b -> bedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
        DatePicker checkinDate = new DatePicker(LocalDate.now());
        DatePicker contractEndDate = new DatePicker(LocalDate.now().plusYears(1));

        // 身份证校验 + 自动提取
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
            if (!idCardText.isEmpty() && validateIdCard(idCardText) == null) return null;

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
            refresh(table, ctx.getElderlyDao().findAll());
            return e;
        });
        dlg.showAndWait();
    }

    private String validateIdCard(String id) {
        if (id == null || id.length() != 18) return null;
        for (int i = 0; i < 17; i++) {
            if (!Character.isDigit(id.charAt(i))) return null;
        }
        char last = id.charAt(17);
        if (!Character.isDigit(last) && last != 'X' && last != 'x') return null;
        String birth = id.substring(6, 14);
        try {
            LocalDate.parse(birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8));
        } catch (Exception e) { return null; }
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) sum += (id.charAt(i) - '0') * weights[i];
        char expected = checkCodes[sum % 11];
        if (expected == 'X' && (last == 'X' || last == 'x')) return birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
        if (Character.toUpperCase(last) != expected) return null;
        return birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
    }

    // ==================== 护理记录 ====================
    private VBox buildCareRecords() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CareRecord> table = new TableView<>();
        TableColumn<CareRecord, String> c1 = col("老人ID", "elderlyId");
        TableColumn<CareRecord, String> c2 = col("项目代码", "projectCode");
        TableColumn<CareRecord, String> c3 = col("执行时间", "executeTime");
        TableColumn<CareRecord, String> c4 = col("数量", "quantity");
        TableColumn<CareRecord, String> c5 = col("护工", "nurseName");
        table.getColumns().addAll(c1, c2, c3, c4, c5);

        String nurseName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        refresh(table, ctx.getCareRecordDao().findByNurseName(nurseName));

        Button addBtn = new Button("新增护理记录");
        addBtn.setOnAction(e -> showAddRecordDialog(nurseName, table));

        box.getChildren().addAll(addBtn, table);
        return box;
    }

    private void showAddRecordDialog(String nurseName, TableView<CareRecord> table) {
        Dialog<CareRecord> dlg = new Dialog<>();
        dlg.setTitle("新增护理记录");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        ComboBox<String> elderlyBox = new ComboBox<>();
        ctx.getElderlyDao().findAll().forEach(e -> elderlyBox.getItems().add(e.getId() + " - " + e.getName()));
        // 只显示启用状态的护理项目
        ComboBox<String> projectBox = new ComboBox<>();
        ctx.getCareProjectDao().findAll().stream()
        .filter(p -> "启用".equals(p.getStatus()))
        .forEach(p -> projectBox.getItems().add(p.getCode() + " - " + p.getName()));

        TextField quantity = new TextField("1");
        TextField remark = new TextField();
        DatePicker execDate = new DatePicker(LocalDate.now());

        grid.add(new Label("老人："), 0, 0); grid.add(elderlyBox, 1, 0);
        grid.add(new Label("护理项目："), 0, 1); grid.add(projectBox, 1, 1);
        grid.add(new Label("护理数量："), 0, 2); grid.add(quantity, 1, 2);
        grid.add(new Label("执行日期："), 0, 3); grid.add(execDate, 1, 3);
        grid.add(new Label("备注："), 0, 4); grid.add(remark, 1, 4);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || elderlyBox.getValue() == null || projectBox.getValue() == null) return null;
            String eid = elderlyBox.getValue().split(" - ")[0];
            String pcode = projectBox.getValue().split(" - ")[0];
            String execTime = (execDate.getValue() != null ? execDate.getValue().toString() : LocalDate.now().toString())
                + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            int qty = 1;
            try { qty = Integer.parseInt(quantity.getText().trim()); } catch (Exception ex) { }
            CareRecord cr = new CareRecord(null, eid, pcode,
                execTime, qty, nurseName, remark.getText().trim());
            ctx.getCareRecordDao().insert(cr);
            PersistentIdGenerator.getInstance().save();
            refresh(table, ctx.getCareRecordDao().findByNurseName(nurseName));
            return cr;
        });
        dlg.showAndWait();
    }

    // ==================== 服务管理 ====================
    private VBox buildServices() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<ServiceAssignment> table = new TableView<>();
        TableColumn<ServiceAssignment, String> c1 = col("服务类型", "serviceType");
        TableColumn<ServiceAssignment, String> c2 = col("老人ID", "elderlyId");
        TableColumn<ServiceAssignment, String> c3 = col("开始日期", "startDate");
        TableColumn<ServiceAssignment, String> c4 = col("结束日期", "endDate");
        TableColumn<ServiceAssignment, String> c5 = col("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5);
        refresh(table, ctx.getServiceAssignmentDao().findAll());

        box.getChildren().add(table);
        return box;
    }

    // ==================== 膳食偏好 ====================
    private VBox buildDiet() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<DietPreference> table = new TableView<>();
        TableColumn<DietPreference, String> c1 = col("老人ID", "customerId");
        TableColumn<DietPreference, String> c2 = col("偏好类型", "preferenceType");
        TableColumn<DietPreference, String> c3 = col("描述", "description");
        table.getColumns().addAll(c1, c2, c3);
        refresh(table, ctx.getDietPreferenceDao().findAll());

        box.getChildren().add(table);
        return box;
    }

    // ==================== 外出申请 ====================
    private VBox buildOutRegApply() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<OutRegistration> table = new TableView<>();
        TableColumn<OutRegistration, String> c1 = col("老人ID", "customerId");
        TableColumn<OutRegistration, String> c2 = col("外出时间", "outTime");
        TableColumn<OutRegistration, String> c3 = col("预计归时", "expectedReturnTime");
        TableColumn<OutRegistration, String> c4 = col("实际归时", "actualReturnTime");
        TableColumn<OutRegistration, String> c5 = col("陪同人", "companion");
        TableColumn<OutRegistration, String> c6 = col("事由", "reason");
        TableColumn<OutRegistration, String> c7 = col("审批状态", "approvalStatus");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);

        String nurseName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        refresh(table, ctx.getOutRegistrationDao().findAll().stream()
            .filter(o -> nurseName.equals(o.getCompanion())).toList());

        Button applyBtn = new Button("新增外出申请");
        applyBtn.setOnAction(e -> {
            List<Elderly> activeElders = ctx.getElderlyDao().findAll().stream()
                .filter(el -> "在住".equals(el.getStatus())).toList();
            if (activeElders.isEmpty()) { LoginPane.showAlert(Alert.AlertType.WARNING, "没有在住老人"); return; }

            ChoiceDialog<String> selDlg = new ChoiceDialog<>(
                activeElders.get(0).getId() + " - " + activeElders.get(0).getName(),
                activeElders.stream().map(el -> el.getId() + " - " + el.getName()).toList());
            selDlg.setTitle("选择老人");
            selDlg.setHeaderText("请选择要申请外出的老人");
            selDlg.showAndWait().ifPresent(elderSel -> {
                String elderId = elderSel.split(" - ")[0];
                Dialog<OutRegistration> dlg = new Dialog<>();
                dlg.setTitle("外出申请");
                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
                DatePicker outDate = new DatePicker(LocalDate.now());
                TextField outTimeField = new TextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                DatePicker returnDate = new DatePicker(LocalDate.now().plusDays(1));
                TextField returnTimeField = new TextField("18:00");
                TextField reason = new TextField();
                grid.add(new Label("外出日期："), 0, 0); grid.add(outDate, 1, 0);
                grid.add(new Label("外出时间："), 0, 1); grid.add(outTimeField, 1, 1);
                grid.add(new Label("预计归日："), 0, 2); grid.add(returnDate, 1, 2);
                grid.add(new Label("预计归时："), 0, 3); grid.add(returnTimeField, 1, 3);
                grid.add(new Label("事由："), 0, 4); grid.add(reason, 1, 4);
                dlg.getDialogPane().setContent(grid);
                ButtonType okBtn = new ButtonType("提交", ButtonBar.ButtonData.OK_DONE);
                dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
                dlg.setResultConverter(btn -> {
                    if (btn != okBtn) return null;
                    String outTime = outDate.getValue() + " " + outTimeField.getText().trim();
                    String expectedReturn = returnDate.getValue() + " " + returnTimeField.getText().trim();
                    OutRegistration r = new OutRegistration();
                    r.setCustomerId(elderId);
                    r.setOutTime(outTime);
                    r.setExpectedReturnTime(expectedReturn);
                    r.setCompanion(nurseName);
                    r.setReason(reason.getText().trim());
                    r.setStatus("申请中");
                    r.setApprovalStatus("待审批");
                    ctx.getOutRegistrationDao().insert(r);
                    PersistentIdGenerator.getInstance().save();
                    refresh(table, ctx.getOutRegistrationDao().findAll().stream()
                        .filter(o -> nurseName.equals(o.getCompanion())).toList());
                    return r;
                });
                dlg.showAndWait();
            });
        });

        box.getChildren().addAll(applyBtn, table);
        return box;
    }

    // ==================== 退住申请 ====================
    private VBox buildCheckoutApply() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CheckOut> table = new TableView<>();
        TableColumn<CheckOut, String> c1 = col("老人ID", "customerId");
        TableColumn<CheckOut, String> c2 = col("退住类型", "checkoutType");
        TableColumn<CheckOut, String> c3 = col("退住日期", "checkoutDate");
        TableColumn<CheckOut, String> c4 = col("原因", "reason");
        TableColumn<CheckOut, String> c5 = col("审批状态", "approvalStatus");
        table.getColumns().addAll(c1, c2, c3, c4, c5);

        refresh(table, ctx.getCheckOutDao().findAll());

        Button applyBtn = new Button("新增退住申请");
        applyBtn.setOnAction(e -> {
            List<Elderly> activeElders = ctx.getElderlyDao().findAll().stream()
                .filter(el -> "在住".equals(el.getStatus())).toList();
            if (activeElders.isEmpty()) { LoginPane.showAlert(Alert.AlertType.WARNING, "没有在住老人"); return; }

            ChoiceDialog<String> selDlg = new ChoiceDialog<>(
                activeElders.get(0).getId() + " - " + activeElders.get(0).getName(),
                activeElders.stream().map(el -> el.getId() + " - " + el.getName()).toList());
            selDlg.setTitle("选择老人");
            selDlg.setHeaderText("请选择要申请退住的老人");
            selDlg.showAndWait().ifPresent(elderSel -> {
                String elderId = elderSel.split(" - ")[0];
                Dialog<CheckOut> dlg = new Dialog<>();
                dlg.setTitle("退住申请");
                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
                ComboBox<String> typeBox = new ComboBox<>();
                typeBox.getItems().addAll("正常退住", "死亡退住", "保留床位");
                typeBox.setValue("正常退住");
                DatePicker checkoutDate = new DatePicker(LocalDate.now());
                TextField reason = new TextField();
                grid.add(new Label("退住类型："), 0, 0); grid.add(typeBox, 1, 0);
                grid.add(new Label("退住日期："), 0, 1); grid.add(checkoutDate, 1, 1);
                grid.add(new Label("原因："), 0, 2); grid.add(reason, 1, 2);
                dlg.getDialogPane().setContent(grid);
                ButtonType okBtn = new ButtonType("提交", ButtonBar.ButtonData.OK_DONE);
                dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
                dlg.setResultConverter(btn -> {
                    if (btn != okBtn) return null;
                    CheckOut co = new CheckOut();
                    co.setCustomerId(elderId);
                    co.setCheckoutType(typeBox.getValue());
                    co.setCheckoutDate(checkoutDate.getValue().toString());
                    co.setReason(reason.getText().trim());
                    co.setApprovalStatus("待审批");
                    ctx.getCheckOutDao().insert(co);
                    PersistentIdGenerator.getInstance().save();
                    refresh(table, ctx.getCheckOutDao().findAll());
                    return co;
                });
                dlg.showAndWait();
            });
        });

        box.getChildren().addAll(applyBtn, table);
        return box;
    }

    // ==================== 消息 ====================
    private VBox buildMessages() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        String displayName = user.getRealName() != null ? user.getRealName() : user.getUsername();

        TableView<Message> table = new TableView<>();
        TableColumn<Message, String> c1 = col("内容", "content");
        TableColumn<Message, String> c2 = col("时间", "time");
        TableColumn<Message, String> c3 = tcRead("已读", "isRead");
        table.getColumns().addAll(c1, c2, c3);
        table.setRowFactory(tv -> {
            TableRow<Message> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    Message msg = row.getItem();
                    if (!msg.isRead()) {
                        msg.setRead(true);
                        ctx.getMessageDao().update(msg);
                        refresh(table, ctx.getMessageDao().findByReceiver(displayName));
                    }
                    // 根据消息内容关键字跳转到对应模块
                    String content = msg.getContent() != null ? msg.getContent() : "";
                    if (content.contains("护理") || content.contains("服务") || content.contains("管家")) {
                        switchContent("records");
                    } else if (content.contains("外出") || content.contains("退住")) {
                        switchContent("elderly");
                    } else if (content.contains("膳食") || content.contains("饮食")) {
                        switchContent("diet");
                    } else {
                        switchContent("elderly");
                    }
                }
            });
            return row;
        });

        refresh(table, ctx.getMessageDao().findByReceiver(displayName));

        Button markReadBtn = new Button("标记已读");
        markReadBtn.setOnAction(e -> {
            Message sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择消息"); return; }
            sel.setRead(true);
            ctx.getMessageDao().update(sel);
            PersistentIdGenerator.getInstance().save();
            refresh(table, ctx.getMessageDao().findByReceiver(displayName));
        });

        Button markAllReadBtn = new Button("全部已读");
        markAllReadBtn.setOnAction(e -> {
            List<Message> msgs = ctx.getMessageDao().findByReceiver(displayName);
            for (Message m : msgs) {
                if (!m.isRead()) {
                    m.setRead(true);
                    ctx.getMessageDao().update(m);
                }
            }
            PersistentIdGenerator.getInstance().save();
            refresh(table, ctx.getMessageDao().findByReceiver(displayName));
        });

        HBox btns = new HBox(10, markReadBtn, markAllReadBtn);
        Label hint = new Label("双击未读消息可标记已读并跳转到对应页面");
        hint.setStyle("-fx-text-fill:#7f8c8d; -fx-font-size:12px");

        box.getChildren().addAll(hint, btns, table);
        return box;
    }

    // ==================== 辅助方法 ====================
    private <T> TableColumn<T, String> col(String title, String property) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(120);
        return c;
    }

    private TableColumn<Message, String> tcRead(String title, String property) {
        TableColumn<Message, String> c = new TableColumn<>(title);
        c.setCellValueFactory(data -> {
            boolean val = data.getValue().isRead();
            return new SimpleStringProperty(val ? "是" : "否");
        });
        c.setPrefWidth(80);
        return c;
    }

    private <T> void refresh(TableView<T> table, List<T> data) {
        table.setItems(FXCollections.observableArrayList(data));
    }
}
