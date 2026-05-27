# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建与运行

```bash
# 编译
mvn clean compile

# 运行 JavaFX 桌面应用
mvn javafx:run
```

Maven 项目，JDK 21，非标准目录布局（源码根目录为 `src/`，资源目录为 `resources/`）。

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

**认证**：`LoginContext`（`src/neu/YYZX/common/LoginContext.java`）基于 Token 的内存认证，token 存储在 `ConcurrentHashMap`。登录后根据角色打开 AdminFrame 或 NurseFrame。

**包结构**：`gui` → `service` → `dao` → `model`，另有 `common`（工具类）、`util`（JSON/文件工具）、`view`（旧版命令行菜单）。

**新增实体步骤**：新增实体需创建 Model（POJO）和 DAO（继承 `BaseJsonDao<T>`，实现三个抽象方法），在 `DataInitializer` 中添加 DAO 实例字段、加载调用和 getter。

**数据持久化**：每次 `insert()`/`update()`/`delete()` 都会立即写回 JSON 文件。应用关闭时 `MainApp.stop()` 调用 `DataInitializer.saveAll()` 保存所有数据和 ID 序列。

**GUI 结构**：
- `MainApp.java` — 启动入口
- `LoginPane.java` — 登录、注册、找回密码
- `AdminFrame.java` — 管理员主界面（13 个功能模块，左侧导航切换）
- `NurseFrame.java` — 护工主界面（5 个功能模块，左侧导航切换）
