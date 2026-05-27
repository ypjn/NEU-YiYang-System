# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建与运行

```bash
# 启动后端（Spring Boot，端口 8080）
mvn spring-boot:run

# 启动前端（Vue 3 + Vite）
cd frontend && npm run dev
```

Maven 项目，JDK 21，非标准目录布局（源码根目录为 `src/`，资源目录为 `resources/`）。

## 技术栈

- 后端：Spring Boot 3.4.3 + Spring Web MVC，**无数据库**，数据持久化到 `data/json/` 目录的 JSON 文件
- 前端：Vue 3（Composition API）+ Element Plus + Axios，位于 `frontend/`
- 没有使用 Lombok、JPA、MyBatis

## 架构要点

**依赖管理方式特殊**：没有使用 Spring 的 `@Autowired`/`@Component` 依赖注入。`DataInitializer`（`src/neu/YYZX/service/DataInitializer.java`）是核心单例，持有所有 DAO 实例并在启动时从 JSON 文件加载数据。Controller 通过 `DataInitializer.getInstance()` 获取 DAO 直接操作，**不经过 Service 层**。

**请求链路**：`Controller（继承 BaseController）→ DAO（继承 BaseJsonDao<T>）→ JSON 文件`

- `BaseController`（`src/neu/YYZX/controller/BaseController.java`）：提供 `success()`/`error()` 统一返回 `Map<String, Object>`，通过 `ctx` 属性获取各 DAO
- `BaseJsonDao<T>`（`src/neu/YYZX/dao/BaseJsonDao.java`）：通用 JSON 文件 CRUD 基类，每个实体一个 JSON 文件
- `PersistentIdGenerator`（`src/neu/YYZX/common/PersistentIdGenerator.java`）：前缀+自增序号生成 ID

**认证**：基于 `X-Auth-Token` 请求头，token 存储在 `ConcurrentHashMap` 内存中（非 JWT）。`AuthInterceptor` 拦截 `/api/users`、`/api/employees` 仅允许管理员访问。

**包结构**：`controller` → `service` → `dao` → `model`，另有 `config`（CORS + 拦截器）、`common`（工具类）、`util`（JSON/文件工具）、`view`（控制台菜单界面）。

**双入口**：`YYZXApplication.java`（Spring Boot 主入口）和 `view/mainMenu.java`（命令行菜单，独立运行）。

**新增实体步骤**：新增一个实体需要创建 3 个文件 — ① Model（POJO，放在 `model/`），② DAO（继承 `BaseJsonDao<T>`，实现 `getEntityId`/`setEntityId`/`getTypeReference` 三个抽象方法），③ 在 `DataInitializer` 中添加 DAO 实例字段、加载调用和 getter。

**Controller 返回规范**：所有接口返回 `Map<String, Object>`，格式为 `{"success": true/false, "message": "...", "data": ...}`。`BaseController.success()` 和 `error()` 统一生成此结构。

**数据持久化**：每次 `insert()`/`update()`/`delete()` 都会立即写回 JSON 文件（`BaseJsonDao.save()`），同时 Controller 方法末尾需调用 `saveId()` 持久化 ID 序列。无需手动提交事务。

**认证拦截**：`AuthInterceptor` 仅放行 `/api/auth/login`、`/api/auth/register` 和 OPTIONS 预检。`/api/users` 和 `/api/employees` 路径仅限 admin 角色访问，其余已认证路径 admin/nurse 均可访问。Token 存储在内存 `ConcurrentHashMap`，服务重启即失效。密码明文存储。

**前端状态管理**：不使用 Vuex/Pinia，用户信息和 token 存储在 `sessionStorage`，各组件自行管理局部状态。路由守卫 `beforeEach` 检查 `sessionStorage` 中的 role，不匹配时重定向至对应角色首页。

## 前端路由

- `/login`、`/register` — 登录注册
- `/admin/*` — 管理员端（仪表盘、用户管理、老人管理、护理等级、床位、膳食、日志等 14 个子页）
- `/nurse/*` — 护工端（老人信息、护理记录、服务管理、膳食偏好、消息等 6 个子页）

Axios 基地址 `http://localhost:8080/api`，认证 token 存储在 `sessionStorage`。
