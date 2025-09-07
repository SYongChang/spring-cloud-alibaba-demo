package org.scf.dto.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 小程序登录请求DTO
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Data
@Accessors(chain = true)
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 微信小程序登录凭证code
     */
    @NotBlank(message = "登录凭证不能为空")
    private String code;

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
}
