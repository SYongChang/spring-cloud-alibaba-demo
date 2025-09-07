package org.scf.dto.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 小程序登录响应DTO
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Data
@Accessors(chain = true)
public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 语言
     */
    private String language;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

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
     * 是否首次登录
     */
    private Boolean isFirstLogin;
}
