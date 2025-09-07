package org.scf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Druid监控配置类
 * 使用 druid-spring-boot-3-starter 自动配置
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Configuration
public class DruidConfig {

    // druid-spring-boot-3-starter 会自动配置监控页面和过滤器
    // 监控页面访问地址: http://localhost:9002/druid/
    // 登录用户名: admin
    // 登录密码: admin123
    
    public DruidConfig() {
        log.info("Druid配置类初始化，监控页面将通过application.yml配置自动启用");
        log.info("监控页面访问地址: http://localhost:9002/druid/");
    }
}
