package org.scf.controller;

import lombok.extern.slf4j.Slf4j;
import org.scf.dto.auth.LoginRequest;
import org.scf.dto.auth.LoginResponse;
import org.scf.dto.auth.TokenInfo;
import org.scf.dto.common.ApiResponse;
import org.scf.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 小程序登录
     */
    @PostMapping("/miniprogram/login")
    public ApiResponse<LoginResponse> miniprogramLogin(@Valid @RequestBody LoginRequest loginRequest,
                                                       HttpServletRequest request) {
        log.info("收到小程序登录请求，code: {}, nickname: {}", 
                loginRequest.getCode(), loginRequest.getNickname());
        try {
            String clientIp = getClientIp(request);
            log.debug("客户端IP: {}", clientIp);
            LoginResponse response = authService.miniprogramLogin(loginRequest, clientIp);
            log.info("小程序登录成功，userId: {}", response.getUserId());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("小程序登录失败，code: {}", loginRequest.getCode(), e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<TokenInfo> refreshToken(@RequestParam String refreshToken) {
        log.info("收到刷新令牌请求");
        try {
            TokenInfo tokenInfo = authService.refreshToken(refreshToken);
            log.info("刷新令牌成功，userId: {}", tokenInfo.getUserId());
            return ApiResponse.success(tokenInfo);
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(@RequestParam String accessToken) {
        log.info("收到登出请求");
        try {
            boolean result = authService.logout(accessToken);
            if (result) {
                log.info("登出成功");
                return ApiResponse.success(true);
            } else {
                log.warn("登出失败");
                return ApiResponse.error("登出失败");
            }
        } catch (Exception e) {
            log.error("登出失败", e);
            return ApiResponse.error("登出失败");
        }
    }

    /**
     * 验证令牌
     */
    @GetMapping("/validate")
    public ApiResponse<Boolean> validateToken(@RequestParam String token) {
        log.debug("收到令牌验证请求");
        try {
            boolean valid = authService.validateToken(token);
            log.debug("令牌验证结果: {}", valid);
            return ApiResponse.success(valid);
        } catch (Exception e) {
            log.error("验证令牌失败", e);
            return ApiResponse.error("验证令牌失败");
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        log.debug("获取客户端IP: {}", ip);
        return ip;
    }
}
