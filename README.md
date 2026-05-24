# 固废管理系统（Solid Waste Management System）启动与使用指南

## 1. 系统概述

固废管理系统是一套为无害化处理厂构建的电子台账系统，替代纸质台账，实现固废从**接收 → 处置 → 贮存转运 → 检测**的全流程数字化管理。

- **技术栈**: JDK8 + Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + Vue 2.7 + Element UI
- **数据库**: H2 (开发环境，MySQL兼容模式) / MySQL (生产环境)
- **认证**: Spring Security + JWT 无状态认证
- **权限**: RBAC 五角色（管理员/库管/司机/操作员/检测员）

### 项目结构

```
SolidWasteManager/
├── swm-backend/          # Maven 多模块后端
│   ├── swm-common/       # 公共模块 (实体/DTO/VO/工具/异常)
│   ├── swm-security/     # 安全模块 (JWT Filter + Spring Security Config)
│   ├── swm-system/       # 系统管理 (用户/角色/菜单 RBAC)
│   ├── swm-business/     # 业务台账 (接收/处置/贮存/转运/检测)
│   ├── swm-dict/         # 字典模块 (5个字典)
│   └── swm-api/          # 启动模块 (入口 + 配置文件)
├── swm-frontend/         # Vue 2 前端工程
└── doc/                  # 设计文档
```

---

## 2. 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8.0_202+ | `D:\softwares\JDK8` |
| Maven | 3.9.9 | `D:\softwares\Maven` |
| Node.js | 16.x+ | 前端构建依赖（需单独安装） |
| npm | 8.x+ | 随 Node.js 附带 |

> **注意**: 开发环境使用 H2 内嵌数据库，无需安装 MySQL。生产环境需 MySQL 8.0。

---

## 3. 后端启动

### 3.1 构建项目

```powershell
# 设置 JAVA_HOME（如未配置系统环境变量）
$env:JAVA_HOME = "D:\softwares\JDK8"

# 进入后端目录
cd D:\projects\SolidWasteManager\swm-backend

# Maven 打包（跳过测试）
D:\softwares\Maven\bin\mvn.cmd clean package -DskipTests
```

### 3.2 启动应用

```powershell
# 启动 Spring Boot
$env:JAVA_HOME = "D:\softwares\JDK8"
java -jar swm-api\target\swm-api-1.0.0-SNAPSHOT.jar
```

启动成功标志：

```
Started SwmApplication in XX.XXX seconds
Tomcat started on port(s): 8080 (http)
```

### 3.3 后端访问地址

| 服务 | 地址 |
|------|------|
| API 入口 | `http://localhost:8080` |
| Knife4j 接口文档 | `http://localhost:8080/doc.html` |
| H2 数据库控制台 | `http://localhost:8080/h2-console` |

**H2 控制台连接信息**:
- JDBC URL: `jdbc:h2:file:./data/swm`
- 用户名: `sa`
- 密码: 留空

### 3.4 数据库初始化

应用首次启动时自动执行建表和初始化数据，包含：

- **16 张表**: 5 张系统表 + 5 张字典表 + 5 张业务表 + 1 张日志表
- **1 个管理员账号**: admin / admin123
- **5 种角色**: 管理员(ADMIN) / 库管(KEEPER) / 司机(DRIVER) / 操作员(OPERATOR) / 检测员(TESTER)
- **63 条菜单权限**: 涵盖所有模块的菜单和按钮权限
- **2 条字典数据**: 产废单位、车间、矿源、危废类别、处置工艺各 2 条

> 每次重启都会重新执行初始化脚本（使用 MERGE 语句保证数据不会重复插入）。如需清空全部数据，删除 `swm-api/data/` 目录后重启即可。

---

## 4. 前端启动

### 4.1 安装依赖

```powershell
# 进入前端目录
cd D:\projects\SolidWasteManager\swm-frontend

# 安装依赖（首次运行）
npm install
```

### 4.2 启动开发服务器

```powershell
npm run dev
```

前端开发服务器运行在 `http://localhost:8081`，API 请求自动代理到后端 `http://localhost:8080`。

### 4.3 生产构建

```powershell
npm run build
```

构建产物输出到 `dist/` 目录，可部署到 Nginx 静态目录。

---

## 5. 默认账号与角色

| 账号 | 密码 | 角色 | 权限范围 |
|------|------|------|----------|
| admin | admin123 | 系统管理员(ADMIN) | 全部权限 |
| — | — | 库管员(KEEPER) | 接收台账、贮存台账、转运台账 |
| — | — | 司机(DRIVER) | 仅查看接收列表 |
| — | — | 操作员(OPERATOR) | 处置台账 |
| — | — | 检测员(TESTER) | 检测台账 + 复核 |

> 除 admin 外，其他角色账号需登录后通过 **系统管理 → 用户管理** 创建，并分配对应角色。

---

## 6. API 接口总览

### 6.1 认证接口 `/api/v1/auth`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/login` | 登录 | 无 |
| GET | `/userinfo` | 获取当前用户信息(含菜单树) | 登录 |
| POST | `/logout` | 登出 | 登录 |

### 6.2 接收台账 `/api/v1/receiving`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `?page=1&size=20` | 分页查询 | receiving:list |
| GET | `/{id}` | 详情 | receiving:list |
| POST | | 新增（自动生成批次号） | receiving:add |
| PUT | `/{id}` | 编辑 | receiving:edit |
| DELETE | `/{id}` | 删除（逻辑删除） | receiving:delete |

### 6.3 字典接口 `/api/v1/dict`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/producers/list` | 产废单位下拉列表 | 登录 |
| GET | `/workshops/list` | 车间下拉列表 | 登录 |
| GET | `/mines/list` | 矿源下拉列表 | 登录 |
| GET | `/categories/list` | 危废类别下拉列表 | 登录 |
| GET | `/processes/list` | 处置工艺下拉列表 | 登录 |
| GET | `/producers` | 产废单位分页管理 | ADMIN |
| POST | `/producers` | 新增产废单位 | ADMIN |
| PUT | `/producers/{id}` | 编辑产废单位 | ADMIN |
| DELETE | `/producers/{id}` | 删除产废单位 | ADMIN |
| （其他字典 CRUD 接口类似） | | | ADMIN |

### 6.4 系统管理 `/api/v1/system`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/users` | 用户分页列表 | ADMIN |
| POST | `/users` | 新增用户（默认密码123456） | ADMIN |
| PUT | `/users/{id}` | 编辑用户 | ADMIN |
| DELETE | `/users/{id}` | 删除用户 | ADMIN |
| GET | `/roles` | 角色列表 | ADMIN |

### 6.5 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1779291531549
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 40100 | 未登录/用户名密码错误 |
| 40300 | 无权限 |
| 50000 | 服务器内部错误 |

### 6.6 认证方式

所有受保护的 API 需在请求头携带 JWT Token：

```
Authorization: Bearer <accessToken>
```

Token 有效期 24 小时（86400000ms），可通过 `application.yml` 中 `jwt.expiration` 配置。

---

## 7. 业务操作流程

### 7.1 完整业务流程

```
1. 字典配置 → 2. 接收台账 → 3. 处置台账 → 4. 检测台账 → 5. 库存/转运
```

### 7.2 第一步：录入基础字典数据

使用 admin 账号登录后，进入 **字典管理** 模块，确保以下字典有数据：

1. **产废单位**: 设置 `生产许可证编号后缀`（用于生成批次号）
2. **车间**: 设置 `车间缩写`（用于生成批次号）
3. **矿源**: 设置 `矿源码`（用于生成批次号）
4. **危废类别**: 设置废物类别
5. **处置工艺**: 设置处置工艺类型

> 系统已预置各 2 条示例数据，可直接使用或修改。

### 7.3 第二步：新增接收台账

1. 进入 **接收台账 → 接收列表**，点击"新增"
2. 选择产废单位、车间、矿源、危废类别
3. 输入车牌号、毛重(吨)、皮重(吨)，系统自动计算净重
4. 选择司机（需先在系统管理中创建司机账号）
5. 填写接收日期
6. 提交后系统**自动生成批次号**

### 7.4 批次号生成规则

```
批次号 = {licenseSuffix}-{workshopAbbr}-{mineCode}-{date}

示例: SH-F1-MINE001-20260520

组成部分:
  licenseSuffix = 产废单位.许可证编号后缀   (如 "SH")
  workshopAbbr  = 车间.车间缩写             (如 "F1")
  mineCode      = 矿源.矿源码               (如 "MINE001")
  date          = 接收日期(yyyyMMdd)        (如 "20260520")
```

同一批次日期的重复记录会追加 `-01`、`-02`... 后缀。

### 7.5 后续操作

- **处置台账**: 按批次号选择待处置记录，记录入炉/产出重量，自动计算损耗
- **贮存台账**: 查看库存汇总，支持入库/出库/盘点操作
- **转运台账**: 创建转运单，事务性生成出库+入库记录
- **检测台账**: 采样检测，支持复核流程

---

## 8. Knife4j 接口调试

### 8.1 访问接口文档

浏览器打开 `http://localhost:8080/doc.html`，可以看到所有 API 的分组文档。

### 8.2 在线调试步骤

1. 在 Knife4j 页面找到 **认证接口** 分组
2. 点击 `POST /api/v1/auth/login`
3. 点击"调试"，输入请求体：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

4. 点击"发送"，复制返回的 `accessToken`
5. 点击页面右上角 **Authorize** 按钮
6. 输入 `Bearer <token>` 并保存
7. 现在可以调试所有需要认证的接口

---

## 9. 前端页面结构

```
/login          # 登录页
/dashboard      # 仪表盘首页
/receiving/list # 接收台账列表
/treatment/list # 处置台账列表
/storage/list   # 贮存台账列表
/transfer/list  # 转运台账列表
/testing/list   # 检测台账列表
/system/user    # 用户管理
/system/role    # 角色管理
/system/menu    # 菜单管理
/system/log     # 操作日志
/dict/producer  # 产废单位管理
/dict/workshop  # 车间管理
/dict/mine      # 矿源管理
/dict/category  # 危废类别管理
/dict/process   # 处置工艺管理
/profile        # 个人中心
```

### 前端关键机制

- **路由守卫**: 未登录自动跳转登录页，Token 过期自动退出
- **动态路由**: 根据后端返回的菜单权限动态添加路由
- **按钮权限**: 通过 `v-permission` 指令控制按钮显隐
- **请求拦截**: Axios 自动注入 Token，统一处理错误响应

---

## 10. 项目配置文件参考

### 后端核心配置 (`swm-api/src/main/resources/application.yml`)

```yaml
server:
  port: 8080                          # 服务端口

spring:
  datasource:
    url: jdbc:h2:file:./data/swm;MODE=MySQL;DATABASE_TO_LOWER=TRUE
    username: sa
    password:
    driver-class-name: org.h2.Driver

  sql.init:
    mode: always                       # 每次启动执行初始化脚本
    schema-locations: classpath:schema-h2.sql
    data-locations: classpath:data-h2.sql
    continue-on-error: true            # 忽略重复数据错误

jwt:
  secret: SWM2026SolidWasteManagerSecretKeyForJWTTokenGeneration256Bit!
  expiration: 86400000                 # Token有效期24小时（毫秒）

knife4j:
  enable: true
  setting:
    language: zh-CN
```

### 前端代理配置 (`swm-frontend/vue.config.js`)

```javascript
devServer: {
  port: 8081,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

---

## 11. 常见问题

### Q1: 启动报 `documentationPluginsBootstrapper NullPointerException`

**原因**: Knife4j 3.0.3 使用的 Springfox 与 Spring Boot 2.7.18 的路径匹配策略不兼容。

**解决**: `application.yml` 中已配置 `spring.mvc.pathmatch.matching-strategy: ant_path_matcher`，无需额外操作。

### Q2: 登录提示"用户名或密码错误"

**原因**: 数据库初始化脚本未正确执行，或密码哈希值不匹配。

**解决**: 删除 `swm-api/data/` 目录后重启应用，系统会重新执行完整的建表和数据初始化。

### Q3: 前端 `npm install` 失败

- 确保 Node.js ≥ 16.x：`node -v`
- 尝试清空 npm 缓存：`npm cache clean --force`
- 如使用淘宝镜像：`npm config set registry https://registry.npmmirror.com`

### Q4: 前端页面空白或路由异常

- 检查后端是否已启动在 8080 端口
- 浏览器 F12 查看控制台和网络请求是否有 401/403 错误
- 清除 localStorage 后重新登录

### Q5: H2 控制台无法连接

确保 JDBC URL 填写正确：`jdbc:h2:file:./data/swm`（用户名 `sa`，密码留空）。路径 `./data/swm` 相对于后端启动目录，即 `swm-api/data/`。

---

## 12. 快速验证流程

按以下步骤验证系统是否正常运行：

1. **启动后端**: `java -jar swm-api\target\swm-api-1.0.0-SNAPSHOT.jar`
2. **验证后端**: 浏览器访问 `http://localhost:8080/doc.html`，看到 Knife4j 文档页
3. **测试登录**: 在 Knife4j 中调用登录接口，使用 `admin / admin123`，获取 Token
4. **启动前端**: `cd swm-frontend && npm run dev`
5. **验证前端**: 浏览器访问 `http://localhost:8081`，用 `admin / admin123` 登录
6. **查看菜单**: 登录后应看到 仪表盘、接收台账、处置台账、贮存台账、转运台账、检测台账、系统管理、字典管理 共 8 个模块

---

> **下一步**: 验证通过后，可参考 `doc/design.md` 了解完整的设计方案，或开始录入真实字典数据和业务台账。
