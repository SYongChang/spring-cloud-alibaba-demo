package org.scf.config;

import org.scf.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/miniprogram/login",
                    "/auth/refresh", 
                    "/auth/validate",
                    "/actuator/**",
                    "/health",
                    "/info",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error"
                );
    }
}
