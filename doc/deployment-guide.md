# 固废管理系统 — 生产环境部署方案

## Context

系统开发已基本完成（接收/贮存/处置/转运/检测 5 个台账模块 + 用户/角色/菜单管理）。当前在个人电脑上以开发模式运行（H2 文件数据库 + `java -jar` + 前端 dev server）。需要部署到生产环境，使公司所有员工通过浏览器访问。

核心问题：
1. 系统需要一台**持续运行的服务器**（不能依赖个人电脑随时开机）
2. H2 数据库**必须替换为 MySQL**（H2 不适合生产环境）
3. 前端需要**构建为静态文件**，由 nginx 托管
4. 员工电脑**不需要安装任何东西**，浏览器访问即可

## 部署架构

```
员工浏览器 ──→ 公司局域网/公网 ──→ 服务器 (nginx:80)
                                      ├── /api/v1/* ──→ 反向代理 ──→ Spring Boot :8080
                                      └── /* (静态文件) ──→ Vue 前端页面
                                      └── MySQL :3306
```

## 服务器选型

根据公司规模和预算，有三种方案：

| 方案 | 适用场景 | 月成本 | 说明 |
|------|---------|--------|------|
| **A. 云服务器 (推荐)** | 通用，员工分散办公 | ~50-200元 | 阿里云/腾讯云 2核4G，有公网IP，最省心 |
| **B. 公司内部服务器** | 仅局域网内使用 | 0（已有硬件） | 需要一台空闲电脑/服务器，IT维护网络 |
| **C. 个人电脑充当服务器** | 临时/测试 | 0 | 不推荐：电脑关机则系统不可用 |

**推荐方案 A**：购买一台云服务器（阿里云 ECS / 腾讯云 CVM），2核4G CentOS 7.9，约 100元/月。员工无论在公司还是外出都能访问。

## 部署步骤（以云服务器 CentOS 为例）

### 第一步：环境准备

```bash
# 1. 安装 JDK 8
yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

# 2. 安装 MySQL 8.0
yum install -y mysql-server
systemctl start mysqld
systemctl enable mysqld

# 3. 安装 nginx
yum install -y nginx
systemctl start nginx
systemctl enable nginx
```

### 第二步：初始化数据库

```sql
-- 创建数据库和用户
CREATE DATABASE swm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'swm'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON swm.* TO 'swm'@'localhost';
FLUSH PRIVILEGES;

-- 导入建表语句
USE swm;
SOURCE /opt/swm/01-init-schema.sql;
SOURCE /opt/swm/02-init-data.sql;
```

### 第三步：修改后端配置

在服务器上创建 `application-prod.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/swm?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: swm
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  # 关闭 H2 控制台
  h2:
    console:
      enabled: false
```

### 第四步：构建与部署

**在你的开发电脑上执行：**

```bash
# 1. 构建后端 JAR（MySQL 驱动已在 pom.xml 中，默认 scope=runtime 即可）
cd swm-backend
mvn clean package -DskipTests
# 产出: swm-api/target/swm-api-1.0.0-SNAPSHOT.jar

# 2. 构建前端静态文件
cd swm-frontend
npm run build
# 产出: dist/ 目录

# 3. 上传到服务器
scp swm-api/target/swm-api-1.0.0-SNAPSHOT.jar root@服务器IP:/opt/swm/
scp -r swm-frontend/dist/* root@服务器IP:/opt/swm/html/
scp doc/sql/01-init-schema.sql root@服务器IP:/opt/swm/
scp doc/sql/02-init-data.sql root@服务器IP:/opt/swm/
```

### 第五步：配置 nginx

`/etc/nginx/conf.d/swm.conf`：

```nginx
server {
    listen 80;
    server_name _;  # 或你的域名/IP

    # 前端静态文件
    location / {
        root /opt/swm/html;
        index index.html;
        try_files $uri $uri/ /index.html;  # Vue History 模式
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

重载 nginx：`nginx -s reload`

### 第六步：启动后端服务

推荐使用 **systemd** 管理后端进程，实现开机自启和崩溃重启：

`/etc/systemd/system/swm.service`：

```ini
[Unit]
Description=Solid Waste Manager
After=network.target mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/swm
ExecStart=/usr/bin/java -jar /opt/swm/swm-api-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload
systemctl start swm
systemctl enable swm
```

### 第七步：防火墙配置

```bash
# 开放 80 端口
firewall-cmd --add-port=80/tcp --permanent
firewall-cmd --reload

# 云服务器还需要在控制台安全组中放行 80 端口
```

## 员工访问方式

员工电脑**不需要安装任何软件**，打开浏览器输入地址即可：

- 局域网访问：`http://服务器内网IP`（如 `http://192.168.1.100`）
- 公网访问：`http://公网IP`（如 `http://1.2.3.4`）
- 域名访问：`http://swm.yourcompany.com`（需额外配置 DNS 解析）

管理员预先在系统管理 → 用户管理中为每位员工创建账号，员工用分配的账号密码登录。

## 关键注意事项

1. **MySQL 必须替换 H2**：当前 H2 文件数据库在开发电脑 `swm-backend/data/` 目录下，生产环境不能使用。`01-init-schema.sql` 和 `02-init-data.sql` 已准备好完整的 MySQL 建表和数据脚本。

2. **密码安全**：修改 `application-prod.yml` 中的数据库密码为强密码，修改 MySQL root 密码。

3. **数据备份**：设置 MySQL 定期备份（crontab 每天凌晨备份数据库）。

4. **首次部署**：服务器上部署后，用 admin/admin123 登录，在系统管理中创建员工账号。

5. **HTTPS**：如需安全连接，可申请免费 SSL 证书（Let's Encrypt）配置到 nginx。

## 简单方案（如果不想买服务器）

如果只是小团队（10人以内）局域网使用，可以做最简单的部署：

1. 在你自己的电脑上安装 MySQL（替换 H2）
2. 以 `--spring.profiles.active=prod` 启动后端 JAR
3. 用 nginx 或直接 `npm run build` 后用 nginx 托管前端
4. 告诉同事你电脑的局域网 IP，他们浏览器访问即可

**缺点**：你的电脑必须一直开机，重启/关机后系统不可用。
