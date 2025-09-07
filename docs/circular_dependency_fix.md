# 循环依赖问题修复说明

## 问题描述
在Spring Boot应用启动时出现循环依赖错误：
```
The dependencies of some of the beans in the application context form a cycle:
┌─────┐
|  webConfig (field private org.scf.interceptor.JwtInterceptor org.scf.config.WebConfig.jwtInterceptor)
↑     ↓
|  jwtInterceptor (field private org.scf.service.AuthService org.scf.interceptor.JwtInterceptor.authService)
↑     ↓
|  authServiceImpl (field private org.scf.feign.UserFeignClient org.scf.service.impl.AuthServiceImpl.userFeignClient)
↑     ↓
|  org.scf.feign.UserFeignClient
↑     ↓
|  org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration$EnableWebMvcConfiguration
└─────┘
```

## 解决方案

### 1. 创建TokenValidator工具类
创建了独立的`TokenValidator`类来处理JWT令牌验证，避免在拦截器中直接依赖`AuthService`。

**文件位置**: `cloud-auth/src/main/java/org/scf/util/TokenValidator.java`

**主要功能**:
- 验证令牌是否有效
- 从令牌中获取用户ID和openid
- 检查令牌是否在黑名单中

### 2. 重构JwtInterceptor
修改`JwtInterceptor`类，使用`TokenValidator`而不是`AuthService`。

**修改内容**:
- 移除对`AuthService`的依赖
- 使用`TokenValidator`进行令牌验证
- 保持拦截器的核心功能不变

### 3. 更新AuthServiceImpl
在`AuthServiceImpl`中使用`TokenValidator`来处理令牌验证相关的方法。

**修改内容**:
- 注入`TokenValidator`依赖
- 将令牌验证方法委托给`TokenValidator`
- 移除重复的`isTokenBlacklisted`方法

## 修复后的依赖关系

```
WebConfig
    ↓
JwtInterceptor
    ↓
TokenValidator
    ↓
JwtUtil + RedisTemplate

AuthServiceImpl
    ↓
UserFeignClient + WechatService + JwtUtil + RedisTemplate + TokenValidator
```

## 优势

1. **消除循环依赖**: 通过引入`TokenValidator`，打破了原有的循环依赖链
2. **职责分离**: 令牌验证逻辑独立出来，便于维护和测试
3. **代码复用**: `TokenValidator`可以被多个组件使用
4. **性能优化**: 避免了Spring的循环依赖处理开销

## 测试验证

修复后，应用应该能够正常启动，不再出现循环依赖错误。可以通过以下方式验证：

1. 启动应用，检查是否还有循环依赖错误
2. 测试JWT拦截器功能是否正常
3. 测试认证服务功能是否正常

## 注意事项

1. 确保`TokenValidator`中的方法都是线程安全的
2. 如果后续需要添加新的令牌验证逻辑，应该在`TokenValidator`中添加
3. 保持`TokenValidator`的独立性，避免引入其他业务依赖

