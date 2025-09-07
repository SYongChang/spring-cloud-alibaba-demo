package org.scf.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expire}")
    private Long accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private Long refreshTokenExpire;

    @Autowired
    private SecretKey jwtSecretKey;

    /**
     * 获取签名密钥
     */
    private SecretKey getSignKey() {
        return jwtSecretKey;
    }

    /**
     * 生成访问令牌
     *
     * @param userId 用户ID
     * @param openid 用户openid
     * @return 访问令牌
     */
    public String generateAccessToken(Long userId, String openid) {
        log.debug("生成访问令牌，userId: {}, openid: {}", userId, openid);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);
        claims.put("type", "access");
        
        String token = createToken(claims, accessTokenExpire);
        log.debug("访问令牌生成成功，过期时间: {}秒", accessTokenExpire);
        return token;
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @param openid 用户openid
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long userId, String openid) {
        log.debug("生成刷新令牌，userId: {}, openid: {}", userId, openid);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);
        claims.put("type", "refresh");
        
        String token = createToken(claims, refreshTokenExpire);
        log.debug("刷新令牌生成成功，过期时间: {}秒", refreshTokenExpire);
        return token;
    }

    /**
     * 创建令牌
     *
     * @param claims 数据声明
     * @param expire 过期时间（秒）
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims, Long expire) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusSeconds(expire);
        
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString()) // JTI
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public Claims getClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("JWT令牌解析成功");
            return claims;
        } catch (JwtException e) {
            log.error("解析JWT令牌失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Long userId = claims.get("userId", Long.class);
            log.debug("从令牌获取用户ID: {}", userId);
            return userId;
        }
        log.debug("无法从令牌获取用户ID");
        return null;
    }

    /**
     * 从令牌中获取openid
     *
     * @param token 令牌
     * @return openid
     */
    public String getOpenidFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            String openid = claims.get("openid", String.class);
            log.debug("从令牌获取openid: {}", openid);
            return openid;
        }
        log.debug("无法从令牌获取openid");
        return null;
    }

    /**
     * 从令牌中获取令牌类型
     *
     * @param token 令牌
     * @return 令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.get("type", String.class);
        }
        return null;
    }

    /**
     * 从令牌中获取JTI
     *
     * @param token 令牌
     * @return JTI
     */
    public String getJtiFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.getId();
        }
        return null;
    }

    /**
     * 验证令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                Date expiration = claims.getExpiration();
                boolean expired = expiration.before(new Date());
                log.debug("令牌过期检查，过期时间: {}, 是否过期: {}", expiration, expired);
                return expired;
            }
            log.debug("无法获取令牌过期信息，视为过期");
            return true;
        } catch (Exception e) {
            log.error("验证令牌过期时间失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            boolean valid = claims != null && !isTokenExpired(token);
            log.debug("令牌验证结果: {}", valid);
            return valid;
        } catch (Exception e) {
            log.error("验证令牌失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取令牌过期时间
     *
     * @param token 令牌
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.getExpiration();
        }
        return null;
    }

    /**
     * 获取令牌剩余有效时间（秒）
     *
     * @param token 令牌
     * @return 剩余有效时间（秒）
     */
    public Long getRemainingTime(String token) {
        Date expiration = getExpirationFromToken(token);
        if (expiration != null) {
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining > 0 ? remaining / 1000 : 0;
        }
        return 0L;
    }
}
