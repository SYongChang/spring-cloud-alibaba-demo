package org.scf.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 令牌验证工具类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Component
public class TokenValidator {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    /**
     * 验证令牌是否有效
     *
     * @param token 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        log.debug("开始验证令牌");
        try {
            // 1. 基本验证
            if (!jwtUtil.validateToken(token)) {
                log.debug("令牌基本验证失败");
                return false;
            }

            // 2. 检查是否在黑名单中
            if (isTokenBlacklisted(token)) {
                log.debug("令牌在黑名单中");
                return false;
            }

            log.debug("令牌验证成功");
            return true;
        } catch (Exception e) {
            log.error("验证令牌失败", e);
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        if (validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            log.debug("从令牌获取用户ID: {}", userId);
            return userId;
        }
        log.debug("令牌无效，无法获取用户ID");
        return null;
    }

    /**
     * 从令牌中获取openid
     *
     * @param token 令牌
     * @return openid
     */
    public String getOpenidFromToken(String token) {
        if (validateToken(token)) {
            String openid = jwtUtil.getOpenidFromToken(token);
            log.debug("从令牌获取openid: {}", openid);
            return openid;
        }
        log.debug("令牌无效，无法获取openid");
        return null;
    }

    /**
     * 检查令牌是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String jti = jwtUtil.getJtiFromToken(token);
            if (jti != null && !jti.isEmpty()) {
                boolean blacklisted = redisTemplate.hasKey(BLACKLIST_PREFIX + jti);
                log.debug("检查令牌黑名单，jti: {}, blacklisted: {}", jti, blacklisted);
                return blacklisted;
            }
            return false;
        } catch (Exception e) {
            log.error("检查令牌黑名单失败", e);
            return false;
        }
    }

    /**
     * 将令牌加入黑名单
     */
    public void addToBlacklist(String token) {
        try {
            String jti = jwtUtil.getJtiFromToken(token);
            if (jti != null && !jti.isEmpty()) {
                java.util.Date expiration = jwtUtil.getExpirationFromToken(token);
                if (expiration != null) {
                    long ttl = expiration.getTime() - System.currentTimeMillis();
                    if (ttl > 0) {
                        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, "1", ttl, java.util.concurrent.TimeUnit.MILLISECONDS);
                        log.debug("令牌已加入黑名单，jti: {}, ttl: {}ms", jti, ttl);
                    }
                }
            }
        } catch (Exception e) {
            log.error("添加令牌到黑名单失败", e);
        }
    }
}
