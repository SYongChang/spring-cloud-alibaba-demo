package org.scf.service;

import org.scf.dto.auth.LoginRequest;
import org.scf.dto.auth.LoginResponse;
import org.scf.dto.auth.TokenInfo;

/**
 * 认证服务接口
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
public interface AuthService {

    /**
     * 小程序登录
     *
     * @param loginRequest 登录请求
     * @param clientIp 客户端IP
     * @return 登录响应
     */
    LoginResponse miniprogramLogin(LoginRequest loginRequest, String clientIp);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    TokenInfo refreshToken(String refreshToken);

    /**
     * 登出
     *
     * @param accessToken 访问令牌
     * @return 是否登出成功
     */
    boolean logout(String accessToken);

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);

    /**
     * 从令牌中获取openid
     *
     * @param token 令牌
     * @return openid
     */
    String getOpenidFromToken(String token);
}
