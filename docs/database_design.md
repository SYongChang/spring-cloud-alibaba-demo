# 小程序用户数据库设计文档

## 📋 表结构概览

### 1. miniprogram_user（小程序用户表）
**核心用户信息存储表**

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | 20 | 是 | 自增 | 用户ID，主键 |
| openid | varchar | 64 | 是 | - | 微信小程序openid，唯一标识 |
| unionid | varchar | 64 | 否 | NULL | 微信开放平台unionid |
| session_key | varchar | 64 | 否 | NULL | 微信会话密钥 |
| nickname | varchar | 100 | 否 | NULL | 用户昵称 |
| avatar_url | varchar | 500 | 否 | NULL | 用户头像URL |
| phone | varchar | 20 | 否 | NULL | 手机号 |
| status | tinyint | 1 | 是 | 1 | 用户状态：1-正常，0-禁用 |
| last_login_time | datetime | - | 否 | NULL | 最后登录时间 |
| login_count | int | 11 | 是 | 0 | 登录次数 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | - | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | 1 | 是 | 0 | 逻辑删除标记 |

**索引设计：**
- 主键：`id`
- 唯一索引：`uk_openid` (openid)
- 普通索引：`idx_phone` (phone), `idx_status` (status)

### 2. user_login_log（用户登录日志表）
**记录用户登录行为**

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | 20 | 是 | 自增 | 日志ID |
| user_id | bigint | 20 | 是 | - | 用户ID |
| openid | varchar | 64 | 是 | - | 用户openid |
| login_ip | varchar | 50 | 是 | - | 登录IP地址 |
| login_status | tinyint | 1 | 是 | 1 | 登录状态：1-成功，0-失败 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- 主键：`id`
- 普通索引：`idx_user_id` (user_id), `idx_openid` (openid)

### 3. jwt_blacklist（JWT黑名单表）
**管理失效的JWT Token**

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | 20 | 是 | 自增 | 记录ID |
| token_id | varchar | 100 | 是 | - | JWT Token唯一标识 |
| user_id | bigint | 20 | 是 | - | 用户ID |
| expire_time | datetime | - | 是 | - | Token过期时间 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- 主键：`id`
- 唯一索引：`uk_token_id` (token_id)
- 普通索引：`idx_user_id` (user_id)

### 4. system_config（系统配置表）
**存储系统配置信息**

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | 20 | 是 | 自增 | 配置ID |
| config_key | varchar | 100 | 是 | - | 配置键 |
| config_value | text | - | 否 | NULL | 配置值 |
| description | varchar | 200 | 否 | NULL | 配置描述 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引设计：**
- 主键：`id`
- 唯一索引：`uk_config_key` (config_key)

## 🔧 配置说明

### 微信小程序配置
```sql
-- 微信小程序AppID
INSERT INTO system_config (config_key, config_value, description) 
VALUES ('miniprogram.appid', 'your_appid', '微信小程序AppID');

-- 微信小程序Secret
INSERT INTO system_config (config_key, config_value, description) 
VALUES ('miniprogram.secret', 'your_secret', '微信小程序Secret');
```

### JWT配置
```sql
-- JWT签名密钥
INSERT INTO system_config (config_key, config_value, description) 
VALUES ('jwt.secret', 'your_jwt_secret', 'JWT签名密钥');

-- 访问令牌过期时间（秒）
INSERT INTO system_config (config_key, config_value, description) 
VALUES ('jwt.access_expire', '7200', '访问令牌过期时间（秒）');

-- 刷新令牌过期时间（秒）
INSERT INTO system_config (config_key, config_value, description) 
VALUES ('jwt.refresh_expire', '604800', '刷新令牌过期时间（秒）');
```

## 📊 数据关系图

```
miniprogram_user (用户表)
    ├── id (主键)
    ├── openid (唯一标识)
    └── unionid (多应用统一标识)

user_login_log (登录日志表)
    ├── user_id → miniprogram_user.id
    └── openid → miniprogram_user.openid

jwt_blacklist (JWT黑名单表)
    ├── user_id → miniprogram_user.id
    └── token_id (JWT唯一标识)

system_config (系统配置表)
    ├── config_key (配置键)
    └── config_value (配置值)
```

## 🚀 部署说明

### 1. 完整版部署
```bash
# 执行完整版SQL
mysql -u username -p database_name < sql/miniprogram_user_tables.sql
```

### 2. 简化版部署
```bash
# 执行简化版SQL
mysql -u username -p database_name < sql/miniprogram_user_simple.sql
```

### 3. 配置修改
部署完成后，需要修改 `system_config` 表中的配置值：
- `miniprogram.appid`: 替换为实际的微信小程序AppID
- `miniprogram.secret`: 替换为实际的微信小程序Secret
- `jwt.secret`: 替换为强随机的JWT签名密钥

## 🔒 安全建议

1. **数据库安全**
   - 使用强密码
   - 限制数据库访问IP
   - 定期备份数据

2. **敏感信息保护**
   - `session_key` 和 `jwt.secret` 等敏感信息需要加密存储
   - 生产环境建议使用环境变量或配置中心

3. **索引优化**
   - 根据实际查询场景调整索引
   - 定期分析慢查询日志

4. **数据清理**
   - 定期清理过期的登录日志
   - 清理过期的JWT黑名单记录

