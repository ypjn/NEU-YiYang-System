# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建与运行

```bash
# 编译
mvn clean compile

# 运行 JavaFX 桌面应用
mvn javafx:run
```

Maven 项目，JDK 21，非标准目录布局（源码根目录为 `src/`，资源目录为 `resources/`）。项目无自动化测试。

## 技术栈

- JavaFX 21 桌面 GUI，无 Web 层，无数据库
- 数据持久化到 `data/json/` 目录的 JSON 文件
- Jackson 2.17 用于 JSON 序列化
- 没有使用 Spring、Lombok、JPA、MyBatis

## 架构要点

**入口**：`neu.YYZX.gui.MainApp` — JavaFX Application，启动时初始化数据并显示登录窗口。

**核心单例**：`DataInitializer`（`src/neu/YYZX/service/DataInitializer.java`）持有所有 DAO 实例，在 `init()` 中从 JSON 文件加载数据并补齐默认数据。

**请求链路**：`GUI 事件 → Service → DAO（继承 BaseJsonDao<T>）→ JSON 文件`

- `BaseJsonDao<T>`（`src/neu/YYZX/dao/BaseJsonDao.java`）：通用 JSON 文件 CRUD 基类，子类需实现 `getEntityId`/`setEntityId`/`getTypeReference`
- `PersistentIdGenerator`（`src/neu/YYZX/common/PersistentIdGenerator.java`）：前缀+自增序号生成 ID，状态持久化到 `data/last-id.json`
- Service 通过构造函数接收 DAO 实例，所有 DAO 由 `DataInitializer.getInstance().getXxxDao()` 获取

**认证**：`LoginContext`（`src/neu/YYZX/common/LoginContext.java`）基于 Token 的内存认证，token 存储在 `ConcurrentHashMap`。登录后根据角色打开 AdminFrame 或 NurseFrame。

**操作审计**：`AuditLogger`（`src/neu/YYZX/common/AuditLogger.java`）通过 `ThreadLocal<User>` 跟踪当前操作用户。GUI 层在处理请求前需调用 `AuditLogger.setCurrentUser(user)`，Service 层通过 `AuditLogger.log(action, target, detail)` 记录操作日志（自动写入 `OperationLog` 并持久化 ID 序列）。

**包结构**：`gui` → `service` → `dao` → `model`，另有 `common`（工具类）、`util`（JSON/文件工具）、`view`（旧版命令行菜单）。

**新增实体步骤**：新增实体需创建 Model（POJO）和 DAO（继承 `BaseJsonDao<T>`，实现三个抽象方法），在 `DataInitializer` 中添加 DAO 实例字段、加载调用和 getter。

**数据持久化**：每次 `insert()`/`update()`/`delete()` 都会立即写回 JSON 文件。应用关闭时 `MainApp.stop()` 调用 `DataInitializer.saveAll()` 保存所有数据和 ID 序列。

**GUI 结构**：
- `MainApp.java` — 启动入口，`stop()` 时调用 `saveAll()` 持久化数据
- `LoginPane.java` — 登录、注册（含密保问题）、找回密码
- `AdminFrame.java` — 管理员主界面（13 个模块，左侧 ToggleButton 导航）
- `NurseFrame.java` — 护工主界面（5 个模块，左侧 ToggleButton 导航）
- 模块切换有 200ms 淡入动效（FadeTransition），选中按钮蓝色高亮 + 加粗标题

**遗留代码**：`view/`（CLI 菜单）和 `service/DataManager.java` 是旧版遗留，已不再使用。`resources/` 目录为空（Spring Boot 配置文件已删除）。

**数据目录**：`data/json/` 下每个实体一个 JSON 文件，`data/last-id.json` 存 ID 序列。整个 `data/` 目录已被 .gitignore 排除。

**安全注意**：密码明文存储。Token 存内存 `ConcurrentHashMap`，进程重启即失效。
