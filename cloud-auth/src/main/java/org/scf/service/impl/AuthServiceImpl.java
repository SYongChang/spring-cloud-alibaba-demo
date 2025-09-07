package org.scf.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.scf.dto.auth.LoginRequest;
import org.scf.dto.auth.LoginResponse;
import org.scf.dto.auth.TokenInfo;
import org.scf.dto.common.ApiResponse;
import org.scf.dto.wechat.WechatLoginResult;
import org.scf.entity.user.MiniprogramUser;
import org.scf.enums.ErrorCode;
import org.scf.exception.BusinessException;
import org.scf.feign.UserFeignClient;
import org.scf.service.AuthService;
import org.scf.service.WechatService;
import org.scf.util.JwtUtil;
import org.scf.util.TokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private WechatService wechatService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TokenValidator tokenValidator;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_PREFIX = "auth:user:";
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse miniprogramLogin(LoginRequest loginRequest, String clientIp) {
        log.info("开始处理小程序登录请求，code: {}, clientIp: {}", loginRequest.getCode(), clientIp);
        try {
            // 1. 调用微信API获取openid和session_key
            WechatLoginResult wechatResult = wechatService.login(loginRequest.getCode());
            if (!wechatResult.isSuccess()) {
                throw new BusinessException(ErrorCode.WECHAT_LOGIN_FAILED.getCode(), 
                    "微信登录失败: " + wechatResult.getErrmsg());
            }

            String openid = wechatResult.getOpenid();
            String sessionKey = wechatResult.getSessionKey();
            String unionid = wechatResult.getUnionid();
            log.info("微信登录成功，openid: {}, unionid: {}", openid, unionid);

            // 2. 查询用户是否存在
            log.debug("查询用户是否存在，openid: {}", openid);
            ApiResponse<MiniprogramUser> userResponse = userFeignClient.getUserByOpenid(openid);
            MiniprogramUser user;
            boolean isFirstLogin = false;

            if (userResponse.getCode() == 200 && userResponse.getData() != null) {
                // 用户已存在，更新session_key
                user = userResponse.getData();
                log.info("用户已存在，userId: {}, nickname: {}", user.getId(), user.getNickname());
                user.setSessionKey(sessionKey);
                if (StrUtil.isNotBlank(unionid)) {
                    user.setUnionid(unionid);
                }
                // 更新用户信息
                updateUserFromRequest(user, loginRequest);
            } else {
                // 用户不存在，创建新用户
                log.info("用户不存在，创建新用户，openid: {}", openid);
                user = new MiniprogramUser();
                user.setOpenid(openid);
                user.setSessionKey(sessionKey);
                user.setUnionid(unionid);
                user.setStatus(1);
                user.setLoginCount(0);
                user.setDeleted(0);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                // 设置用户信息
                updateUserFromRequest(user, loginRequest);
                
                // 保存新用户到数据库
                log.debug("保存新用户到数据库，openid: {}", openid);
                ApiResponse<MiniprogramUser> saveResponse = userFeignClient.createUser(user);
                if (saveResponse.getCode() == 200 && saveResponse.getData() != null) {
                    user = saveResponse.getData();
                    log.info("新用户创建成功，userId: {}, openid: {}", user.getId(), openid);
                } else {
                    log.error("新用户创建失败，openid: {}, 错误信息: {}", openid, saveResponse.getMessage());
                    throw new BusinessException(ErrorCode.USER_CREATE_FAILED.getCode(), "用户创建失败");
                }
                
                isFirstLogin = true;
            }

            // 3. 生成JWT令牌
            log.debug("生成JWT令牌，userId: {}, openid: {}", user.getId(), openid);
            String accessToken = jwtUtil.generateAccessToken(user.getId(), openid);
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), openid);

            // 4. 计算令牌过期时间
            LocalDateTime accessTokenExpireTime = LocalDateTime.now().plusSeconds(7200); // 2小时
            LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(604800); // 7天

            // 5. 缓存用户信息和令牌
            log.debug("缓存用户信息和令牌，userId: {}", user.getId());
            cacheUserInfo(user, accessToken, refreshToken);

            // 6. 更新用户登录信息
            if (user.getId() != null) {
                log.debug("更新用户登录信息，userId: {}, clientIp: {}", user.getId(), clientIp);
                userFeignClient.updateLastLoginInfo(user.getId(), clientIp);
                userFeignClient.incrementLoginCount(user.getId());
            }

            // 7. 构建响应
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId());
            response.setOpenid(user.getOpenid());
            response.setNickname(user.getNickname());
            response.setAvatarUrl(user.getAvatarUrl());
            response.setGender(user.getGender());
            response.setCountry(user.getCountry());
            response.setProvince(user.getProvince());
            response.setCity(user.getCity());
            response.setLanguage(user.getLanguage());
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setAccessTokenExpireTime(accessTokenExpireTime);
            response.setRefreshTokenExpireTime(refreshTokenExpireTime);
            response.setIsFirstLogin(isFirstLogin);

            log.info("小程序登录成功，userId: {}, isFirstLogin: {}", user.getId(), isFirstLogin);
            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("小程序登录失败", e);
            throw new BusinessException(ErrorCode.LOGIN_FAILED.getCode(), "登录失败");
        }
    }

    @Override
    public TokenInfo refreshToken(String refreshToken) {
        log.info("开始刷新令牌");
        try {
            // 1. 验证刷新令牌
            if (!jwtUtil.validateToken(refreshToken)) {
                log.warn("刷新令牌验证失败");
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID.getCode(), "刷新令牌无效");
            }

            // 2. 检查令牌是否在黑名单中
            if (tokenValidator.isTokenBlacklisted(refreshToken)) {
                log.warn("刷新令牌已被拉黑");
                throw new BusinessException(ErrorCode.TOKEN_BLACKLISTED.getCode(), "刷新令牌已被拉黑");
            }

            // 3. 获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String openid = jwtUtil.getOpenidFromToken(refreshToken);
            log.info("从刷新令牌获取用户信息，userId: {}, openid: {}", userId, openid);
            
            if (userId == null || StrUtil.isBlank(openid)) {
                log.warn("刷新令牌中无法获取有效用户信息");
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID.getCode(), "刷新令牌无效");
            }

            // 4. 生成新的访问令牌
            log.debug("生成新的访问令牌和刷新令牌，userId: {}", userId);
            String newAccessToken = jwtUtil.generateAccessToken(userId, openid);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, openid);

            // 5. 将旧令牌加入黑名单
            log.debug("将旧刷新令牌加入黑名单");
            tokenValidator.addToBlacklist(refreshToken);

            // 6. 缓存新的令牌
            cacheUserInfo(null, newAccessToken, newRefreshToken);

            // 7. 构建响应
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setAccessToken(newAccessToken);
            tokenInfo.setRefreshToken(newRefreshToken);
            tokenInfo.setAccessTokenExpireTime(LocalDateTime.now().plusSeconds(7200));
            tokenInfo.setRefreshTokenExpireTime(LocalDateTime.now().plusSeconds(604800));
            tokenInfo.setUserId(userId);
            tokenInfo.setOpenid(openid);

            log.info("令牌刷新成功，userId: {}", userId);
            return tokenInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID.getCode(), "刷新令牌失败");
        }
    }

    @Override
    public boolean logout(String accessToken) {
        log.info("开始处理登出请求");
        try {
            // 1. 验证访问令牌
            if (!jwtUtil.validateToken(accessToken)) {
                log.warn("登出时访问令牌验证失败");
                return false;
            }

            // 2. 将访问令牌加入黑名单
            log.debug("将访问令牌加入黑名单");
            tokenValidator.addToBlacklist(accessToken);

            // 3. 清除缓存
            String jti = jwtUtil.getJtiFromToken(accessToken);
            if (StrUtil.isNotBlank(jti)) {
                log.debug("清除令牌缓存，jti: {}", jti);
                redisTemplate.delete(TOKEN_PREFIX + jti);
            }

            log.info("登出成功");
            return true;
        } catch (Exception e) {
            log.error("登出失败", e);
            return false;
        }
    }

    @Override
    public boolean validateToken(String token) {
        return tokenValidator.validateToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return tokenValidator.getUserIdFromToken(token);
    }

    @Override
    public String getOpenidFromToken(String token) {
        return tokenValidator.getOpenidFromToken(token);
    }

    /**
     * 从请求中更新用户信息
     */
    private void updateUserFromRequest(MiniprogramUser user, LoginRequest request) {
        if (StrUtil.isNotBlank(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StrUtil.isNotBlank(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (StrUtil.isNotBlank(request.getCountry())) {
            user.setCountry(request.getCountry());
        }
        if (StrUtil.isNotBlank(request.getProvince())) {
            user.setProvince(request.getProvince());
        }
        if (StrUtil.isNotBlank(request.getCity())) {
            user.setCity(request.getCity());
        }
        if (StrUtil.isNotBlank(request.getLanguage())) {
            user.setLanguage(request.getLanguage());
        }
        if (StrUtil.isNotBlank(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StrUtil.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        user.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 缓存用户信息
     */
    private void cacheUserInfo(MiniprogramUser user, String accessToken, String refreshToken) {
        try {
            String accessJti = jwtUtil.getJtiFromToken(accessToken);
            String refreshJti = jwtUtil.getJtiFromToken(refreshToken);
            
            if (StrUtil.isNotBlank(accessJti)) {
                redisTemplate.opsForValue().set(TOKEN_PREFIX + accessJti, accessToken, 7200, TimeUnit.SECONDS);
                log.debug("缓存访问令牌，jti: {}", accessJti);
            }
            
            if (StrUtil.isNotBlank(refreshJti)) {
                redisTemplate.opsForValue().set(TOKEN_PREFIX + refreshJti, refreshToken, 604800, TimeUnit.SECONDS);
                log.debug("缓存刷新令牌，jti: {}", refreshJti);
            }
            
            if (user != null && user.getId() != null) {
                redisTemplate.opsForValue().set(USER_PREFIX + user.getId(), user, 7200, TimeUnit.SECONDS);
                log.debug("缓存用户信息，userId: {}", user.getId());
            }
        } catch (Exception e) {
            log.error("缓存用户信息失败", e);
        }
    }

}
