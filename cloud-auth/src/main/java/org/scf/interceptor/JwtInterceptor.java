package org.scf.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.scf.util.TokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT拦截器
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenValidator tokenValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        log.debug("JWT拦截器处理请求: {} {}", method, requestPath);
        
        // 跳过不需要认证的路径
        if (isSkipPath(requestPath)) {
            log.debug("跳过认证路径: {}", requestPath);
            return true;
        }

        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求缺少有效的Authorization头: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的认证令牌\"}");
            return false;
        }

        // 提取令牌
        String token = authHeader.substring(7);
        log.debug("提取到令牌，长度: {}", token.length());
        
        // 验证令牌
        if (!tokenValidator.validateToken(token)) {
            log.warn("令牌验证失败: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"认证令牌无效或已过期\"}");
            return false;
        }

        // 将用户信息添加到请求属性中
        Long userId = tokenValidator.getUserIdFromToken(token);
        String openid = tokenValidator.getOpenidFromToken(token);
        
        log.debug("令牌验证成功，userId: {}, openid: {}", userId, openid);
        
        request.setAttribute("userId", userId);
        request.setAttribute("openid", openid);
        request.setAttribute("token", token);

        return true;
    }

    /**
     * 判断是否跳过认证的路径
     */
    private boolean isSkipPath(String requestPath) {
        // 登录相关接口不需要认证
        if (requestPath.startsWith("/auth/miniprogram/login") ||
            requestPath.startsWith("/auth/refresh") ||
            requestPath.startsWith("/auth/validate")) {
            return true;
        }
        
        // 健康检查接口不需要认证
        if (requestPath.startsWith("/actuator/") ||
            requestPath.equals("/health") ||
            requestPath.equals("/info")) {
            return true;
        }
        
        // 静态资源不需要认证
        if (requestPath.startsWith("/static/") ||
            requestPath.startsWith("/css/") ||
            requestPath.startsWith("/js/") ||
            requestPath.startsWith("/images/")) {
            return true;
        }
        
        return false;
    }
}
