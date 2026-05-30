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
            {"🍽 膳食偏好", "diet"}, {"💬 我的消息", "messages"}
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
        "diet", "膳食偏好", "messages", "我的消息"
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
        TableColumn<Elderly, String> c4 = col("床位ID", "bedId");
        TableColumn<Elderly, String> c5 = col("护理等级", "nursingLevelCode");
        TableColumn<Elderly, String> c6 = col("状态", "status");
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);
        refresh(table, ctx.getElderlyDao().findAll());

        searchField.textProperty().addListener((o, ov, nv) -> {
            if (nv.trim().isEmpty()) refresh(table, ctx.getElderlyDao().findAll());
            else refresh(table, ctx.getElderlyDao().findByName(nv.trim()));
        });

        box.getChildren().addAll(searchField, table);
        return box;
    }

    // ==================== 护理记录 ====================
    private VBox buildCareRecords() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<CareRecord> table = new TableView<>();
        TableColumn<CareRecord, String> c1 = col("老人ID", "elderlyId");
        TableColumn<CareRecord, String> c2 = col("项目代码", "projectCode");
        TableColumn<CareRecord, String> c3 = col("执行时间", "executeTime");
        TableColumn<CareRecord, String> c4 = col("护工", "nurseName");
        table.getColumns().addAll(c1, c2, c3, c4);

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
        ComboBox<String> projectBox = new ComboBox<>();
        ctx.getCareProjectDao().findAll().forEach(p -> projectBox.getItems().add(p.getCode() + " - " + p.getName()));
        TextField remark = new TextField();
        DatePicker execDate = new DatePicker(LocalDate.now());

        grid.add(new Label("老人："), 0, 0); grid.add(elderlyBox, 1, 0);
        grid.add(new Label("护理项目："), 0, 1); grid.add(projectBox, 1, 1);
        grid.add(new Label("执行日期："), 0, 2); grid.add(execDate, 1, 2);
        grid.add(new Label("备注："), 0, 3); grid.add(remark, 1, 3);

        dlg.getDialogPane().setContent(grid);
        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn != okBtn || elderlyBox.getValue() == null || projectBox.getValue() == null) return null;
            String eid = elderlyBox.getValue().split(" - ")[0];
            String pcode = projectBox.getValue().split(" - ")[0];
            String execTime = (execDate.getValue() != null ? execDate.getValue().toString() : LocalDate.now().toString())
                + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            CareRecord cr = new CareRecord(null, eid, pcode,
                execTime, nurseName, remark.getText().trim());
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

    // ==================== 消息 ====================
    private VBox buildMessages() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TableView<Message> table = new TableView<>();
        TableColumn<Message, String> c1 = col("内容", "content");
        TableColumn<Message, String> c2 = col("时间", "time");
        TableColumn<Message, String> c3 = tcRead("已读", "isRead");
        table.getColumns().addAll(c1, c2, c3);

        String displayName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        List<Message> msgs = ctx.getMessageDao().findByReceiver(displayName);
        refresh(table, msgs);

        Button markReadBtn = new Button("标记已读");
        markReadBtn.setOnAction(e -> {
            Message sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { LoginPane.showAlert(Alert.AlertType.WARNING, "请先选择消息"); return; }
            sel.setRead(true);
            ctx.getMessageDao().update(sel);
            refresh(table, ctx.getMessageDao().findByReceiver(displayName));
        });

        box.getChildren().addAll(markReadBtn, table);
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
