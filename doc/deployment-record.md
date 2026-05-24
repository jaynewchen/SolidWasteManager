# 华为云服务器部署操作记录

## 部署概览

| 项目 | 详情 |
|------|------|
| 部署日期 | 2026-05-23 |
| 云平台 | 华为云 ECS |
| 公网 IP | 121.36.210.16 |
| 操作系统 | Huawei Cloud EulerOS 2.0 (x86_64) |
| 服务器配置 | 1核 / 807MB RAM / 40GB 磁盘 |
| 访问地址 | http://121.36.210.16 |
| 管理员账号 | admin / admin123 |

## 部署架构

```
用户浏览器 → :80 (nginx)
              ├── /           → 前端静态文件 (/opt/swm/html)
              └── /api/       → 反向代理 → Spring Boot :8080
                                          → MySQL :3306
```

---

## 一、本地构建

### 1.1 构建后端 JAR

在开发电脑 `D:\projects\SolidWasteManager\swm-backend` 目录执行：

```bash
mvn clean package -DskipTests
```

产出文件：`swm-api/target/swm-api-1.0.0-SNAPSHOT.jar`

### 1.2 构建前端静态文件

```bash
cd swm-frontend
npm run build
```

产出目录：`dist/`

### 1.3 准备数据库初始化脚本

项目中的 SQL 脚本位于 `swm-backend/doc/sql/`：
- `01-init-schema.sql` — 建表语句
- `02-init-data.sql` — 初始数据（需修复 `USE swm_db;` → `USE swm;`）

---

## 二、SSH 密钥配置

在开发电脑生成专用 SSH 密钥对，避免每次输入密码：

```powershell
ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa_swm -N '""'
```

上传公钥到服务器：

```powershell
# 使用 Python paramiko 上传（因服务器未安装 ssh-copy-id）
python -c "
import paramiko
ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('121.36.210.16', username='root', password='Admin123')
ssh.exec_command('mkdir -p ~/.ssh')
with open('$env:USERPROFILE\.ssh\id_rsa_swm.pub') as f:
    ssh.exec_command('echo \"' + f.read().strip() + '\" >> ~/.ssh/authorized_keys')
ssh.close()
"
```

后续所有 SSH/SCP 命令使用：`ssh -i ~/.ssh/id_rsa_swm root@121.36.210.16`

---

## 三、服务器环境搭建

### 3.1 配置 Swap（2GB）

服务器只有 807MB 内存，必须配置 swap 才能运行 Java 应用：

```bash
dd if=/dev/zero of=/swapfile bs=1M count=2048
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile swap swap defaults 0 0' >> /etc/fstab
```

验证：

```bash
free -h
swapon --show
```

### 3.2 安装 JDK 8

```bash
yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel
```

安装版本：OpenJDK 1.8.0_432 (BiSheng)

### 3.3 安装 MySQL 8.0

```bash
yum install -y mysql-server
systemctl start mysqld
systemctl enable mysqld
```

### 3.4 安装 nginx

```bash
yum install -y nginx
```

### 3.5 创建应用目录

```bash
mkdir -p /opt/swm/html
```

---

## 四、MySQL 配置与初始化

### 4.1 MySQL 低内存优化

服务器内存仅 807MB，需要对 MySQL 进行内存优化。

编辑 `/etc/my.cnf`，在 `[mysqld]` 段添加：

```ini
# Low memory config for 1GB server
innodb_buffer_pool_size = 64M
innodb_log_file_size = 16M
max_connections = 50
performance_schema = OFF
table_open_cache = 128
tmp_table_size = 8M
max_heap_table_size = 8M
```

重启 MySQL：

```bash
systemctl restart mysqld
```

### 4.2 设置 MySQL root 密码

初始登录（无密码）后设置：

```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Swm@2026!';
FLUSH PRIVILEGES;
```

### 4.3 创建数据库和应用用户

```sql
CREATE DATABASE swm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'swm'@'localhost' IDENTIFIED BY 'SwmDb@2026!';
GRANT ALL PRIVILEGES ON swm.* TO 'swm'@'localhost';
FLUSH PRIVILEGES;
```

### 4.4 导入建表语句

```bash
mysql -u root -p'Swm@2026!' < /opt/swm/01-init-schema.sql
```

说明：原始脚本中 `CREATE DATABASE IF NOT EXISTS swm_db` 和 `USE swm_db;` 与实际数据库名 `swm` 不一致，但不影响建表（手动连入 swm 库执行）。

### 4.5 导入初始数据

原始 `02-init-data.sql` 存在两个问题：
1. `USE swm_db;` — 数据库名错误（应为 `swm`）
2. BCrypt hash `$2a$10$...` 在 BiSheng JDK 上无法验证

处理方式：
1. 手动插入 admin 用户，使用 Python bcrypt 生成新 hash

```python
import bcrypt
print(bcrypt.hashpw(b'admin123', bcrypt.gensalt()).decode())
# 输出: $2b$10$zZACGnhHY7gKsaCqBLSx4./kzEDXDPze/GVV.Zdwr73mfPJn3bebu
```

```sql
INSERT INTO sys_user (id, username, password, real_name, phone, email, status)
VALUES (1, 'admin', '$2b$10$zZACGnhHY7gKsaCqBLSx4./kzEDXDPze/GVV.Zdwr73mfPJn3bebu',
        '系统管理员', '13800000000', 'admin@swm.com', 1);
```

2. 将修正后的数据脚本 `02-init-data-fixed.sql` 上传并导入（使用 `USE swm;` 和 `INSERT IGNORE`）：

```bash
mysql -u root -p'Swm@2026!' < /opt/swm/02-init-data-fixed.sql
```

导入内容：
- 5 个角色（ADMIN, KEEPER, DRIVER, OPERATOR, TESTER）
- 62 条菜单/按钮权限
- 94 条角色-菜单关联
- 1 条用户-角色关联（admin → ADMIN）
- 10 条字典数据（产废单位、车间、矿源、危废类别、处置工艺各 2 条）

---

## 五、应用部署

### 5.1 上传文件到服务器

```powershell
# 上传 JAR
scp -i ~/.ssh/id_rsa_swm D:\projects\SolidWasteManager\swm-backend\swm-api\target\swm-api-1.0.0-SNAPSHOT.jar root@121.36.210.16:/opt/swm/

# 上传前端文件
scp -i ~/.ssh/id_rsa_swm -r D:\projects\SolidWasteManager\swm-frontend\dist\* root@121.36.210.16:/opt/swm/html/

# 上传 SQL 脚本
scp -i ~/.ssh/id_rsa_swm D:\projects\SolidWasteManager\swm-backend\doc\sql\01-init-schema.sql root@121.36.210.16:/opt/swm/
scp -i ~/.ssh/id_rsa_swm D:\projects\SolidWasteManager\swm-backend\doc\sql\02-init-data-fixed.sql root@121.36.210.16:/opt/swm/
```

### 5.2 创建生产环境配置

在服务器创建 `/opt/swm/application-prod.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/swm?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: swm
    password: SwmDb@2026!
    driver-class-name: com.mysql.cj.jdbc.Driver
  h2:
    console:
      enabled: false
```

### 5.3 配置 systemd 服务

创建 `/etc/systemd/system/swm.service`：

```ini
[Unit]
Description=Solid Waste Manager
After=network.target mysqld.service
Wants=mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/swm
ExecStart=/usr/bin/java -Xmx256m -Xms128m -jar /opt/swm/swm-api-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=15
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

JVM 参数说明：
- `-Xmx256m` — 最大堆内存 256MB（总内存 807MB，预留空间给 OS/MySQL/nginx）
- `-Xms128m` — 初始堆内存 128MB

启动并设置开机自启：

```bash
systemctl daemon-reload
systemctl start swm
systemctl enable swm
```

### 5.4 配置 nginx

创建 `/etc/nginx/conf.d/swm.conf`：

```nginx
server {
    listen 80;
    server_name _;

    location / {
        root /opt/swm/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

重载 nginx：

```bash
nginx -t && nginx -s reload
```

---

## 六、防火墙配置

服务器使用 `firewalld` 未被激活。华为云安全组需要在**华为云控制台**中配置：

1. 登录华为云控制台 → 弹性云服务器 → 安全组
2. 添加入方向规则：允许 TCP 80 端口（0.0.0.0/0）

---

## 七、遇到的问题及解决

### 问题 1：PowerShell BOM 字符污染

**现象**：通过 PowerShell 管道向 SSH 传输文件时，自动在文件开头添加 UTF-8 BOM（`﻿`），导致：
- `nginx: [emerg] unknown directive "﻿server"` — nginx 无法启动
- `command not found` — shell 脚本执行失败
- MySQL 无法识别 SQL 语句

**解决**：放弃管道方式，改为在本地用 `.NET` 方法（`[System.IO.File]::WriteAllText` 指定 UTF8 无 BOM）写入文件后，通过 `scp` 上传。

### 问题 2：JDBC URL 编码错误

**现象**：应用启动失败，报错 `Unsupported character encoding 'utf8mb4'`

**原因**：`application-prod.yml` 中 JDBC URL 写的是 `characterEncoding=utf8mb4`，但 `utf8mb4` 是 MySQL 字符集名，不是 Java 支持的字符编码名。

**解决**：改为 `characterEncoding=UTF-8`

### 问题 3：BCrypt 密码哈希不兼容

**现象**：登录返回 `code=40100`，"用户名或密码错误"

**原因**：SQL 脚本中预置的 BCrypt hash `$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi` 在 BiSheng JDK 上无法正确验证 `admin123`。该 hash 可能是在不同 JDK 版本或 BCrypt 库下生成的。

**解决**：在本地用 Python bcrypt 重新生成 `$2b$` 版本的 hash，通过 MySQL UPDATE 替换 admin 用户密码。

### 问题 4：02-init-data.sql 数据库名错误

**现象**：`02-init-data.sql` 开头有 `USE swm_db;`，但实际数据库名为 `swm`。执行后数据未导入，所有业务表为空。

**解决**：创建修正版 `02-init-data-fixed.sql`，改为 `USE swm;`，并使用 `INSERT IGNORE` 避免重复插入冲突。

### 问题 5：应用自动重启循环

**现象**：由于问题 2 导致应用启动失败，systemd 的 `Restart=on-failure` 会每 15 秒重新尝试启动，造成重启循环。

**解决**：先 `systemctl stop swm` 停止服务，修复配置后重新 `systemctl start swm`。

---

## 八、运维常用命令

### 服务管理

```bash
# 查看后端服务状态
systemctl status swm

# 重启后端服务
systemctl restart swm

# 查看后端日志
journalctl -u swm -f

# 查看最近 100 条日志
journalctl -u swm -n 100

# 重载 nginx
nginx -s reload

# 重启 MySQL
systemctl restart mysqld
```

### 数据库备份

```bash
# 备份整个数据库
mysqldump -u root -p'Swm@2026!' swm > /opt/swm/backup/swm_$(date +%Y%m%d).sql

# 设置 crontab 每日凌晨 2 点自动备份
echo '0 2 * * * mysqldump -u root -p"Swm@2026!" swm > /opt/swm/backup/swm_$(date +\%Y\%m\%d).sql' | crontab -
```

### 更新部署

```bash
# 1. 本地构建新 JAR 和前端
mvn clean package -DskipTests
cd swm-frontend && npm run build && cd ..

# 2. 上传到服务器
scp -i ~/.ssh/id_rsa_swm swm-api/target/swm-api-1.0.0-SNAPSHOT.jar root@121.36.210.16:/opt/swm/
scp -i ~/.ssh/id_rsa_swm -r swm-frontend/dist/* root@121.36.210.16:/opt/swm/html/

# 3. 重启后端服务
ssh -i ~/.ssh/id_rsa_swm root@121.36.210.16 "systemctl restart swm"
```

---

## 九、验证清单

| 验证项 | 方法 | 预期结果 |
|--------|------|----------|
| MySQL 运行 | `systemctl status mysqld` | active (running) |
| nginx 运行 | `systemctl status nginx` | active (running) |
| 后端运行 | `systemctl status swm` | active (running) |
| 端口监听 | `ss -tlnp \| grep -E ':80\|:8080\|:3306'` | 3 个端口均在 LISTEN |
| 前端可访问 | 浏览器访问 `http://121.36.210.16` | 显示登录页面 |
| API 可访问 | `curl http://121.36.210.16/api/v1/auth/login` | 返回 JSON |
| 登录功能 | POST `/api/v1/auth/login` admin/admin123 | code=200, 返回 token |
| 内存使用 | `free -h` | available > 100MB |

### 当前验证结果（2026-05-23）

```
服务状态: swm.service active (running)
内存使用: 507Mi used / 807Mi total / 170Mi available
Swap使用: 80Mi / 2.0Gi
磁盘使用: 5.1G / 40G (14%)
端口监听: 80 (nginx), 8080 (java), 3306 (mysql) — 全部正常
登录测试: code=200, 返回 ADMIN 角色 + 62 权限
数据库: 1 用户, 5 角色, 62 菜单, 94 角色菜单关联, 1 用户角色关联, 10 字典数据
```
