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
        AuditLogger.setCurrentUser(user);
        this.stage = new Stage();
        stage.setTitle("东软颐养中心 - 护工端 [" + (user.getRealName() != null ? user.getRealName() : user.getUsername()) + "]");
        buildUI();
    }

    public void show() {
        stage.setScene(new Scene(root, 1000, 680));
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
        VBox nav = new VBox(3);
        nav.setPadding(new Insets(10));
        nav.setPrefWidth(145);
        nav.setStyle("-fx-background-color:#2c3e50");

        String[][] items = {
            {"老人信息", "elderly"}, {"护理记录", "records"},
            {"膳食偏好", "diet"}, {"健康记录", "health"}, {"外出申请", "outreg"},
            {"退住申请", "checkout"}, {"我的消息", "messages"}
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
        "elderly", "老人信息", "records", "护理记录",
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
            case "diet": content = buildDiet(); break;
            case "health": content = buildHealthRecords(); break;
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

        table.setRowFactory(tv -> {
            TableRow<Elderly> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showElderlyInfoDialog(row.getItem());
                }
            });
            return row;
        });

        searchField.textProperty().addListener((o, ov, nv) -> {
            if (nv.trim().isEmpty()) refresh(table, ctx.getElderlyDao().findAll());
            else refresh(table, ctx.getElderlyDao().findByName(nv.trim()));
        });

        Button checkinBtn = new Button("老人入住");
        Button editBtn = new Button("编辑信息");
        checkinBtn.setOnAction(e -> {
            long availBeds = ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus())).count();
            if (availBeds == 0) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "没有空闲床位，请先由管理员创建房间和床位");
                return;
            }
            showCheckinDialog(table);
        });
        editBtn.setOnAction(e -> {
            Elderly sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择要编辑的老人"); return; }
            Elderly latest = ctx.getElderlyDao().findById(sel.getId());
            if (latest == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "该老人信息已不存在"); return; }
            showEditElderlyDialog(latest, table);
        });

        HBox topRow = new HBox(10, searchField, checkinBtn, editBtn);
        box.getChildren().addAll(topRow, table);
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
        TextField familyMember = new TextField();
        TextField emContact = new TextField();
        TextField emPhone = new TextField();
        ComboBox<String> bedBox = new ComboBox<>();
        ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus()))
            .forEach(b -> bedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
        if (!bedBox.getItems().isEmpty()) bedBox.setValue(bedBox.getItems().get(0));
        DatePicker checkinDate = new DatePicker(LocalDate.now());
        DatePicker contractEndDate = new DatePicker(LocalDate.now().plusYears(1));

        // 身份证校验 + 自动提取，锁定年龄和出生日期
        Label idCardMsg = new Label();
        idCardMsg.setStyle("-fx-font-size:11px");
        idCard.textProperty().addListener((o, ov, nv) -> {
            String result = validateIdCard(nv);
            if (result == null) {
                idCard.setStyle("-fx-border-color:red; -fx-border-width:1px");
                idCardMsg.setText(nv.length() == 18 ? "身份证格式错误" : "");
                idCardMsg.setStyle("-fx-text-fill:red; -fx-font-size:11px");
                age.setDisable(false); birthDate.setDisable(false); gender.setDisable(false);
            } else {
                idCard.setStyle("");
                String birth = nv.substring(6, 14);
                LocalDate bd = LocalDate.parse(birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8));
                birthDate.setValue(bd);
                int calculatedAge = java.time.Period.between(bd, LocalDate.now()).getYears();
                age.setText(String.valueOf(calculatedAge));
                int seqDigit = Integer.parseInt(nv.substring(14, 17));
                gender.setValue(seqDigit % 2 == 1 ? "男" : "女");
                age.setDisable(true); birthDate.setDisable(true); gender.setDisable(true);
                idCardMsg.setText("✓ 出生: " + result + "  性别: " + gender.getValue() + "  年龄: " + calculatedAge);
                idCardMsg.setStyle("-fx-text-fill:green; -fx-font-size:11px");
            }
        });

        grid.add(new Label("姓名："), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("身份证："), 0, 1); grid.add(idCard, 1, 1);
        grid.add(idCardMsg, 2, 1, 2, 1);
        grid.add(new Label("年龄："), 0, 2); grid.add(age, 1, 2);
        grid.add(new Label("出生日期："), 2, 2); grid.add(birthDate, 3, 2);
        grid.add(new Label("性别："), 0, 3); grid.add(gender, 1, 3);
        grid.add(new Label("血型："), 2, 3); grid.add(bloodType, 3, 3);
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
                e.setAge(java.time.Period.between(birthDate.getValue(), LocalDate.now()).getYears());
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
            Map<String, Object> undoData = new HashMap<>();
            undoData.put("type", "elderly_checkin");
            undoData.put("elderlyId", e.getId());
            undoData.put("bedId", bedId);
            AuditLogger.logReversible("老人入住", "老人管理", e.getName(), undoData);
            notifyButlerByElderly(e.getId(), "已办理入住");
            notifyAdmins("新老人【" + e.getName() + "】已办理入住");
            refresh(table, ctx.getElderlyDao().findAll());
            return e;
        });
        dlg.showAndWait();
    }

    private void showEditElderlyDialog(Elderly elder, TableView<Elderly> table) {
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
        if (elder.getBedId() != null && !elder.getBedId().isEmpty()) {
            Bed curBed = ctx.getBedDao().findById(elder.getBedId());
            if (curBed != null) bedBox.getItems().add(curBed.getBedId() + " - " + curBed.getBedNo() + " (当前)");
        }
        ctx.getBedDao().findAll().stream().filter(b -> "available".equals(b.getStatus()))
            .forEach(b -> bedBox.getItems().add(b.getBedId() + " - " + b.getBedNo()));
        if (elder.getBedId() != null && !elder.getBedId().isEmpty() && !bedBox.getItems().isEmpty())
            bedBox.setValue(bedBox.getItems().get(0));
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
                int calculatedAge = java.time.Period.between(bd, LocalDate.now()).getYears();
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
                    if (elder.getBedId() != null && !elder.getBedId().isEmpty()) {
                        Bed oldBed = ctx.getBedDao().findById(elder.getBedId());
                        if (oldBed != null) { oldBed.setStatus("available"); ctx.getBedDao().update(oldBed); }
                    }
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
                elder.setAge(java.time.Period.between(birthDate.getValue(), LocalDate.now()).getYears());
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
            refresh(table, ctx.getElderlyDao().findAll());
            return elder;
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
        TableColumn<CareRecord, String> c0 = new TableColumn<>("老人姓名");
        c0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getElderlyId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getElderlyId());
        });
        TableColumn<CareRecord, String> c2 = col("项目代码", "projectCode");
        TableColumn<CareRecord, String> c3 = col("执行时间", "executeTime");
        TableColumn<CareRecord, String> c4 = col("数量", "quantity");
        TableColumn<CareRecord, String> c5 = col("护工", "nurseName");
        table.getColumns().addAll(c0, c2, c3, c4, c5);

        String nurseName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        refresh(table, ctx.getCareRecordDao().findByNurseName(nurseName));

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

        Button addBtn = new Button("新增护理记录");
        addBtn.setOnAction(e -> showAddRecordDialog(nurseName, table));

        box.getChildren().addAll(addBtn, table);
        return box;
    }

    private void showAddRecordDialog(String nurseName, TableView<CareRecord> table) {
        Dialog<CareRecord> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("新增护理记录");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        ComboBox<String> elderlyBox = new ComboBox<>();
        ctx.getElderlyDao().findAll().forEach(e -> elderlyBox.getItems().add(e.getId() + " - " + e.getName()));

        ComboBox<String> projectBox = new ComboBox<>();
        Label remainHint = new Label("");

        // 选择老人后，只显示已购买且剩余次数>0的项目
        elderlyBox.setOnAction(e -> {
            projectBox.getItems().clear();
            remainHint.setText("");
            if (elderlyBox.getValue() == null) return;
            String eid = elderlyBox.getValue().split(" - ")[0];
            List<CustomerCareProject> owned = ctx.getCustomerCareProjectDao().findByCustomerId(eid);
            for (CustomerCareProject ccp : owned) {
                if (ccp.getQuantity() <= 0) continue;
                CareProject cp = ctx.getCareProjectDao().findByCode(ccp.getProjectCode());
                if (cp == null || !"启用".equals(cp.getStatus())) continue;
                projectBox.getItems().add(ccp.getProjectCode() + " - " + cp.getName()
                    + " (剩余" + ccp.getQuantity() + "次)");
            }
            if (projectBox.getItems().isEmpty()) {
                remainHint.setText("该老人暂无可用护理项目，请先在服务关注中购买");
                remainHint.setStyle("-fx-text-fill:#dc3545; -fx-font-size:12px");
            }
        });

        // 选项目后显示剩余次数
        projectBox.setOnAction(e -> {
            if (elderlyBox.getValue() == null || projectBox.getValue() == null) return;
            String eid = elderlyBox.getValue().split(" - ")[0];
            String pcode = projectBox.getValue().split(" - ")[0];
            CustomerCareProject ccp = ctx.getCustomerCareProjectDao().findByCustomerAndProject(eid, pcode);
            if (ccp != null && ccp.getQuantity() > 0) {
                remainHint.setText("剩余次数：" + ccp.getQuantity() + "，本次最多可执行 " + ccp.getQuantity() + " 次");
                remainHint.setStyle("-fx-text-fill:#28a745; -fx-font-size:12px");
            }
        });

        TextField quantity = new TextField("1");
        TextField remark = new TextField();
        DatePicker execDate = new DatePicker(LocalDate.now());

        grid.add(new Label("老人："), 0, 0); grid.add(elderlyBox, 1, 0);
        grid.add(new Label("护理项目："), 0, 1); grid.add(projectBox, 1, 1);
        grid.add(new Label("护理数量："), 0, 2); grid.add(quantity, 1, 2);
        grid.add(new Label("执行日期："), 0, 3); grid.add(execDate, 1, 3);
        grid.add(new Label("备注："), 0, 4); grid.add(remark, 1, 4);
        grid.add(remainHint, 0, 5, 2, 1);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || elderlyBox.getValue() == null || projectBox.getValue() == null) return null;
            String eid = elderlyBox.getValue().split(" - ")[0];
            String pcode = projectBox.getValue().split(" - ")[0];
            int qty = 1;
            try { qty = Integer.parseInt(quantity.getText().trim()); } catch (Exception ex) { }
            // 检查剩余次数是否足够
            CustomerCareProject ccp = ctx.getCustomerCareProjectDao().findByCustomerAndProject(eid, pcode);
            if (ccp == null || ccp.getQuantity() <= 0) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "剩余次数不足，请先购买或续费");
                return null;
            }
            if (qty > ccp.getQuantity()) {
                LoginPane.showAlert(Alert.AlertType.WARNING, "本次数量(" + qty + ")超过剩余次数(" + ccp.getQuantity() + ")，已自动调整为剩余次数");
                qty = ccp.getQuantity();
            }
            // 扣减已购项目次数
            ccp.setQuantity(ccp.getQuantity() - qty);
            ctx.getCustomerCareProjectDao().update(ccp);

            String execTime = (execDate.getValue() != null ? execDate.getValue().toString() : LocalDate.now().toString())
                + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            CareRecord cr = new CareRecord(null, eid, pcode,
                execTime, qty, nurseName, remark.getText().trim());
            ctx.getCareRecordDao().insert(cr);
            PersistentIdGenerator.getInstance().save();
            notifyButlerByElderly(eid, "执行了护理项目【" + pcode + "】，扣减" + qty + "次，剩余" + ccp.getQuantity() + "次");
            refresh(table, ctx.getCareRecordDao().findByNurseName(nurseName));
            return cr;
        });
        dlg.showAndWait();
    }

    // ==================== 膳食偏好 ====================
    private VBox buildDiet() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<DietPreference> table = new TableView<>();
        TableColumn<DietPreference, String> c1 = new TableColumn<>("老人姓名");
        c1.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getCustomerId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getCustomerId());
        });
        TableColumn<DietPreference, String> c2 = col("口味", "taste");
        TableColumn<DietPreference, String> c3 = col("饮食建议", "dietaryAdvice");
        TableColumn<DietPreference, String> c4 = col("过敏原", "allergies");
        TableColumn<DietPreference, String> c5 = col("忌口", "taboos");
        table.getColumns().addAll(c1, c2, c3, c4, c5);
        refresh(table, ctx.getDietPreferenceDao().findAll());

        table.setRowFactory(tv -> {
            TableRow<DietPreference> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Elderly e = ctx.getElderlyDao().findById(row.getItem().getCustomerId());
                    if (e != null) showElderlyInfoDialog(e);
                }
            });
            return row;
        });

        Button addBtn = new Button("添加膳食偏好");
        addBtn.setOnAction(e -> {
            List<Elderly> activeElders = ctx.getElderlyDao().findAll().stream()
                .filter(el -> "在住".equals(el.getStatus())).toList();
            if (activeElders.isEmpty()) { LoginPane.showAlert(Alert.AlertType.WARNING, "没有在住老人"); return; }

            ChoiceDialog<String> selDlg = new ChoiceDialog<>(
                activeElders.get(0).getId() + " - " + activeElders.get(0).getName(),
                activeElders.stream().map(el -> el.getId() + " - " + el.getName()).toList());
            selDlg.setTitle("选择老人");
            selDlg.setHeaderText("请选择要添加膳食偏好的老人");
            selDlg.showAndWait().ifPresent(elderSel -> {
                String elderId = elderSel.split(" - ")[0];
                String elderName = elderSel.split(" - ")[1];
                Dialog<DietPreference> dlg = new Dialog<>();
                dlg.initOwner(stage);
                dlg.setTitle("添加膳食偏好 - " + elderName);
                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
                TextField taste = new TextField();
                taste.setPromptText("如：偏清淡、喜甜食、重口味");
                TextField advice = new TextField();
                advice.setPromptText("如：建议低盐低脂、多补充蛋白质");
                TextField allergies = new TextField();
                allergies.setPromptText("如：花生、海鲜、牛奶");
                TextField taboos = new TextField();
                taboos.setPromptText("如：忌辛辣、忌油腻、忌生冷");
                grid.add(new Label("口味："), 0, 0); grid.add(taste, 1, 0);
                grid.add(new Label("饮食建议："), 0, 1); grid.add(advice, 1, 1);
                grid.add(new Label("过敏原："), 0, 2); grid.add(allergies, 1, 2);
                grid.add(new Label("忌口："), 0, 3); grid.add(taboos, 1, 3);
                dlg.getDialogPane().setContent(grid);
                ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
                dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
                dlg.setResultConverter(btn -> {
                    if (btn != okBtn) return null;
                    DietPreference dp = new DietPreference();
                    dp.setCustomerId(elderId);
                    dp.setTaste(taste.getText().trim());
                    dp.setDietaryAdvice(advice.getText().trim());
                    dp.setAllergies(allergies.getText().trim());
                    dp.setTaboos(taboos.getText().trim());
                    return dp;
                });
                dlg.showAndWait().ifPresent(dp -> {
                    ctx.getDietPreferenceDao().insert(dp);
                    PersistentIdGenerator.getInstance().save();
                    notifyButlerByElderly(elderId, "的膳食偏好已被护工添加");
                    refresh(table, ctx.getDietPreferenceDao().findAll());
                    Map<String, Object> undoData = new HashMap<>();
                    undoData.put("type", "diet_preference");
                    undoData.put("preferenceId", dp.getPreferenceId());
                    AuditLogger.logReversible("添加膳食偏好", "膳食管理", elderName, undoData);
                });
            });
        });

        Button editBtn = new Button("修改");
        editBtn.setOnAction(e -> {
            DietPreference sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            Elderly elder = ctx.getElderlyDao().findById(sel.getCustomerId());
            String elderName = elder != null ? elder.getName() : sel.getCustomerId();

            Dialog<DietPreference> dlg = new Dialog<>();
            dlg.initOwner(stage);
            dlg.setTitle("修改膳食偏好 - " + elderName);
            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
            TextField taste = new TextField(sel.getTaste() != null ? sel.getTaste() : "");
            TextField advice = new TextField(sel.getDietaryAdvice() != null ? sel.getDietaryAdvice() : "");
            TextField allergies = new TextField(sel.getAllergies() != null ? sel.getAllergies() : "");
            TextField taboos = new TextField(sel.getTaboos() != null ? sel.getTaboos() : "");
            grid.add(new Label("口味："), 0, 0); grid.add(taste, 1, 0);
            grid.add(new Label("饮食建议："), 0, 1); grid.add(advice, 1, 1);
            grid.add(new Label("过敏原："), 0, 2); grid.add(allergies, 1, 2);
            grid.add(new Label("忌口："), 0, 3); grid.add(taboos, 1, 3);
            dlg.getDialogPane().setContent(grid);
            ButtonType okBtn = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
            dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
            dlg.setResultConverter(btn -> {
                if (btn != okBtn) return null;
                sel.setTaste(taste.getText().trim());
                sel.setDietaryAdvice(advice.getText().trim());
                sel.setAllergies(allergies.getText().trim());
                sel.setTaboos(taboos.getText().trim());
                return sel;
            });
            dlg.showAndWait().ifPresent(dp -> {
                ctx.getDietPreferenceDao().update(dp);
                PersistentIdGenerator.getInstance().save();
                notifyButlerByElderly(dp.getCustomerId(), "的膳食偏好已被护工修改");
                refresh(table, ctx.getDietPreferenceDao().findAll());
            });
        });

        Button delBtn = new Button("删除");
        delBtn.setOnAction(e -> {
            DietPreference sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择记录"); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确定删除此膳食偏好吗？", ButtonType.YES, ButtonType.NO);
            confirm.initOwner(stage);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    String customerId = sel.getCustomerId();
                    ctx.getDietPreferenceDao().delete(sel.getPreferenceId());
                    PersistentIdGenerator.getInstance().save();
                    notifyButlerByElderly(customerId, "的膳食偏好已被护工删除");
                    refresh(table, ctx.getDietPreferenceDao().findAll());
                }
            });
        });

        HBox btns = new HBox(10, addBtn, editBtn, delBtn);

        // 食物推荐区域
        Label foodTitle = new Label("推荐食物（请在上方选择一位老人）");
        foodTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1a5276; -fx-padding:10 0 5 0");

        // 选中老人的膳食偏好摘要
        Label dietSummary = new Label();
        dietSummary.setStyle("-fx-font-size:12px; -fx-text-fill:#856404; -fx-padding:0 0 5 0");
        dietSummary.setVisible(false);

        TableView<Food> foodTable = new TableView<>();
        TableColumn<Food, String> fc1 = new TableColumn<>("名称");
        fc1.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        fc1.setPrefWidth(120);
        TableColumn<Food, String> fc2 = new TableColumn<>("类别");
        fc2.setCellValueFactory(new PropertyValueFactory<>("category"));
        fc2.setPrefWidth(80);
        TableColumn<Food, String> fc3 = new TableColumn<>("营养");
        fc3.setCellValueFactory(new PropertyValueFactory<>("nutrition"));
        fc3.setPrefWidth(200);
        TableColumn<Food, String> fc4 = new TableColumn<>("单价");
        fc4.setCellValueFactory(data ->
            new SimpleStringProperty(String.format("%.0f元", data.getValue().getPrice())));
        fc4.setPrefWidth(80);
        foodTable.getColumns().addAll(fc1, fc2, fc3, fc4);

        // 默认显示全部食物
        List<Food> allFoods = ctx.getFoodDao().findAll();
        refresh(foodTable, allFoods);

        // 选中老人时智能推荐：过滤忌口和过敏原不匹配的食物
        table.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            dietSummary.setText("");
            dietSummary.setVisible(false);
            if (nv == null) {
                refresh(foodTable, allFoods);
                foodTitle.setText("推荐食物（请在上方选择一位老人）");
                return;
            }
            Elderly elder = ctx.getElderlyDao().findById(nv.getCustomerId());
            String name = elder != null ? elder.getName() : nv.getCustomerId();

            // 收集忌口关键词
            java.util.Set<String> banKeywords = new java.util.LinkedHashSet<>();
            // 过敏原 → 扩展关键词映射
            java.util.Map<String, String[]> allergenMap = Map.of(
                "鸡蛋", new String[]{"鸡蛋", "蛋"},
                "牛奶", new String[]{"牛奶", "奶"},
                "花生", new String[]{"花生"},
                "海鲜", new String[]{"鱼", "虾", "蟹", "贝", "鱿", "海"}
            );
            if (nv.getAllergies() != null && !nv.getAllergies().isEmpty() && !"无".equals(nv.getAllergies())) {
                for (String a : nv.getAllergies().split("[、，,;；]")) {
                    String kw = a.trim();
                    if (kw.isEmpty()) continue;
                    if (allergenMap.containsKey(kw)) {
                        for (String m : allergenMap.get(kw)) banKeywords.add(m);
                    } else {
                        banKeywords.add(kw);
                    }
                }
            }
            // 忌口 → 扩展关键词映射
            if (nv.getTaboos() != null && !nv.getTaboos().isEmpty()) {
                String[] tabooItems = nv.getTaboos().split("[、，,;；]");
                for (String item : tabooItems) {
                    String t = item.trim().replace("忌", "").replace("少", "").replace("过量", "").trim();
                    if (t.isEmpty()) continue;
                    // 映射表：忌口概念 → 食物匹配关键词
                    java.util.Map<String, String[]> map = Map.ofEntries(
                        Map.entry("辛辣", new String[]{"辣"}),
                        Map.entry("油腻", new String[]{"炸", "油", "肥", "红烧"}),
                        Map.entry("生冷", new String[]{"冷", "凉", "冰"}),
                        Map.entry("海鲜", new String[]{"鱼", "虾", "蟹", "贝"}),
                        Map.entry("高糖", new String[]{"糖", "甜", "蜜"}),
                        Map.entry("糖", new String[]{"糖", "甜", "蜜"}),
                        Map.entry("高盐", new String[]{"咸", "腌", "腊", "酱", "熏"}),
                        Map.entry("过咸", new String[]{"咸", "腌", "腊", "酱", "熏"}),
                        Map.entry("腌制", new String[]{"咸", "腌", "腊", "酱", "熏"}),
                        Map.entry("动物内脏", new String[]{"肝", "腰", "心", "肚", "肠"}),
                        Map.entry("油炸", new String[]{"炸"}),
                        Map.entry("硬食", new String[]{"坚果", "硬", "核桃"}),
                        Map.entry("过硬", new String[]{"坚果", "硬", "核桃"}),
                        Map.entry("豆制品", new String[]{"豆腐", "豆浆", "豆"}),
                        Map.entry("糯米", new String[]{"糯米", "糍"}),
                        Map.entry("黏食", new String[]{"黏", "糍", "年糕"}),
                        Map.entry("肥肉", new String[]{"肥", "扣肉"}),
                        Map.entry("过油", new String[]{"炸", "油", "肥", "红烧"}),
                        Map.entry("浓茶", new String[]{"茶"}),
                        Map.entry("大块肉类", new String[]{"大块"}),
                        Map.entry("香蕉", new String[]{"香蕉"}),
                        Map.entry("橘子", new String[]{"橘子", "橙"}),
                        Map.entry("蛋黄", new String[]{"蛋黄"})
                    );
                    if (map.containsKey(t)) {
                        for (String kw : map.get(t)) banKeywords.add(kw);
                    } else {
                        banKeywords.add(t);
                    }
                }
            }

            // 过滤：名称/营养/备注中包含任一禁止关键词的食物排除
            List<Food> recommended = new java.util.ArrayList<>();
            for (Food f : allFoods) {
                String combined = (f.getFoodName() != null ? f.getFoodName() : "")
                    + " " + (f.getNutrition() != null ? f.getNutrition() : "")
                    + " " + (f.getRemark() != null ? f.getRemark() : "");
                boolean banned = false;
                for (String kw : banKeywords) {
                    if (combined.contains(kw)) { banned = true; break; }
                }
                if (!banned) recommended.add(f);
            }

            // 显示摘要
            java.util.List<String> tags = new java.util.ArrayList<>();
            if (nv.getTaste() != null && !nv.getTaste().isEmpty()) tags.add("口味：" + nv.getTaste());
            if (nv.getAllergies() != null && !nv.getAllergies().isEmpty() && !"无".equals(nv.getAllergies()))
                tags.add("过敏：" + nv.getAllergies());
            if (nv.getTaboos() != null && !nv.getTaboos().isEmpty()) tags.add("忌口：" + nv.getTaboos());
            if (!tags.isEmpty()) {
                dietSummary.setText(" " + String.join("  |  ", tags));
                dietSummary.setVisible(true);
            }

            foodTitle.setText("推荐食物 — " + name + "（已为您过滤，共" + recommended.size() + "款适合）");
            refresh(foodTable, recommended);
        });

        box.getChildren().addAll(btns, table, dietSummary, foodTitle, foodTable);
        return box;
    }

    // ==================== 外出申请 ====================
    private VBox buildOutRegApply() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<OutRegistration> table = new TableView<>();
        TableColumn<OutRegistration, String> c0 = new TableColumn<>("老人姓名");
        c0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getCustomerId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getCustomerId());
        });
        TableColumn<OutRegistration, String> c2 = col("外出时间", "outTime");
        TableColumn<OutRegistration, String> c3 = col("预计归时", "expectedReturnTime");
        TableColumn<OutRegistration, String> c4 = col("实际归时", "actualReturnTime");
        TableColumn<OutRegistration, String> c5 = col("陪同人", "companion");
        TableColumn<OutRegistration, String> c6 = col("事由", "reason");
        TableColumn<OutRegistration, String> c7 = col("审批状态", "approvalStatus");
        table.getColumns().addAll(c0, c2, c3, c4, c5, c6, c7);

        String nurseName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        refresh(table, ctx.getOutRegistrationDao().findAll().stream()
            .filter(o -> nurseName.equals(o.getCompanion())).toList());

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
                dlg.initOwner(stage);
                dlg.setTitle("外出申请");
                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
                DatePicker outDate = new DatePicker(LocalDate.now());
                TextField outTimeField = new TextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                DatePicker returnDate = new DatePicker(LocalDate.now().plusDays(1));
                // 外出日期变化时，预计归日自动设为下一天
                outDate.valueProperty().addListener((o, ov, nv) -> {
                    if (nv != null) returnDate.setValue(nv.plusDays(1));
                });
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
                    notifyAdmins("老人【" + elderSel.split(" - ")[1] + "】申请外出，事由：" + reason.getText().trim());
                    refresh(table, ctx.getOutRegistrationDao().findAll().stream()
                        .filter(o -> nurseName.equals(o.getCompanion())).toList());
                    return r;
                });
                dlg.showAndWait();
            });
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
            Elderly elder = ctx.getElderlyDao().findById(r.getCustomerId());
            if (elder != null && elder.getBedId() != null) {
                Bed bed = ctx.getBedDao().findById(elder.getBedId());
                if (bed != null) { bed.setStatus("occupied"); ctx.getBedDao().update(bed); }
            }
            PersistentIdGenerator.getInstance().save();
            refresh(table, ctx.getOutRegistrationDao().findAll().stream()
                .filter(o -> nurseName.equals(o.getCompanion())).toList());
        });

        HBox btns = new HBox(10, applyBtn, returnBtn);
        box.getChildren().addAll(btns, table);
        return box;
    }

    // ==================== 退住申请 ====================
    private VBox buildCheckoutApply() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CheckOut> table = new TableView<>();
        TableColumn<CheckOut, String> c0 = new TableColumn<>("老人姓名");
        c0.setCellValueFactory(data -> {
            Elderly e = ctx.getElderlyDao().findById(data.getValue().getCustomerId());
            return new SimpleStringProperty(e != null ? e.getName() : data.getValue().getCustomerId());
        });
        TableColumn<CheckOut, String> c2 = col("退住类型", "checkoutType");
        TableColumn<CheckOut, String> c3 = col("退住日期", "checkoutDate");
        TableColumn<CheckOut, String> c4 = col("原因", "reason");
        TableColumn<CheckOut, String> c5 = col("审批状态", "approvalStatus");
        table.getColumns().addAll(c0, c2, c3, c4, c5);

        refresh(table, ctx.getCheckOutDao().findAll());

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
                dlg.initOwner(stage);
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
                    notifyAdmins("老人【" + elderSel.split(" - ")[1] + "】申请退住，类型：" + typeBox.getValue());
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
        TableColumn<HealthRecord, String> c1 = col("记录日期", "recordDate");
        TableColumn<HealthRecord, String> c2 = col("血压", "bloodPressure");
        TableColumn<HealthRecord, String> c3 = col("心率", "heartRate");
        TableColumn<HealthRecord, String> c4 = col("血糖", "bloodSugar");
        TableColumn<HealthRecord, String> c5 = col("体重(kg)", "weight");
        TableColumn<HealthRecord, String> c6 = col("体温(℃)", "temperature");
        TableColumn<HealthRecord, String> c7 = col("备注", "remark");
        table.getColumns().addAll(c0, c1, c2, c3, c4, c5, c6, c7);
        refresh(table, ctx.getHealthRecordDao().findAll());

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
            if ("无 - 全部".equals(sel)) refresh(table, ctx.getHealthRecordDao().findAll());
            else refresh(table, ctx.getHealthRecordDao().findByCustomerId(sel.split(" - ")[0]));
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
                if (sel == null || "无 - 全部".equals(sel)) refresh(table, ctx.getHealthRecordDao().findAll());
                else refresh(table, ctx.getHealthRecordDao().findByCustomerId(sel.split(" - ")[0]));
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
                    if (selValue == null || "无 - 全部".equals(selValue)) refresh(table, ctx.getHealthRecordDao().findAll());
                    else refresh(table, ctx.getHealthRecordDao().findByCustomerId(selValue.split(" - ")[0]));
                }
            });
        });

        HBox btnRow = new HBox(10, addBtn, delBtn);
        box.getChildren().addAll(topRow, table, btnRow);
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
        table.refresh();
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
            Label lbl = new Label(labels[i] + ":");
            lbl.setStyle("-fx-font-weight:bold; -fx-font-size:13px");
            Label val = new Label(values[i]);
            val.setStyle("-fx-font-size:13px");
            val.setWrapText(true);
            grid.add(lbl, 0, i);
            grid.add(val, 1, i);
        }

        Label bedLbl = new Label("床位号:");
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

    /** 根据老人ID通知其管家/护工 */
    private void notifyButlerByElderly(String elderlyId, String actionDesc) {
        try {
            Elderly elder = ctx.getElderlyDao().findById(elderlyId);
            String elderName = elder != null ? elder.getName() : elderlyId;
            List<ServiceAssignment> assignments = ctx.getServiceAssignmentDao().findAll().stream()
                .filter(a -> a.getElderlyId().equals(elderlyId) && "服务中".equals(a.getStatus()))
                .toList();
            for (ServiceAssignment sa : assignments) {
                Employee emp = ctx.getEmployeeDao().findById(sa.getEmployeeId());
                if (emp != null) {
                    Message msg = new Message();
                    msg.setReceiverName(emp.getName());
                    msg.setContent("老人【" + elderName + "】" + actionDesc);
                    msg.setTime(LocalDateTime.now().format(fmt));
                    msg.setRead(false);
                    ctx.getMessageDao().insert(msg);
                }
            }
            if (!assignments.isEmpty()) PersistentIdGenerator.getInstance().save();
        } catch (Exception ignored) {}
    }

    /** 通知所有管理员 */
    private void notifyAdmins(String content) {
        try {
            List<User> admins = ctx.getUserDao().findAll().stream()
                .filter(u -> "admin".equals(u.getRole())).toList();
            for (User admin : admins) {
                Message msg = new Message();
                msg.setReceiverName(admin.getRealName());
                msg.setContent(content);
                msg.setTime(LocalDateTime.now().format(fmt));
                msg.setRead(false);
                ctx.getMessageDao().insert(msg);
            }
            PersistentIdGenerator.getInstance().save();
        } catch (Exception ignored) {}
    }
}
