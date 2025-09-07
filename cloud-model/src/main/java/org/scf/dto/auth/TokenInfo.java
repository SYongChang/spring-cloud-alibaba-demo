package org.scf.dto.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JWT Token信息DTO
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Data
@Accessors(chain = true)
public class TokenInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌过期时间
     */
    private LocalDateTime accessTokenExpireTime;

    /**
     * 刷新令牌过期时间
     */
    private LocalDateTime refreshTokenExpireTime;

    /**
     * Token类型
     */
    private String tokenType = "Bearer";

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户openid
     */
    private String openid;
}
