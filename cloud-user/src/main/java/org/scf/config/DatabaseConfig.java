package org.scf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置类
 * 使用 druid-spring-boot-3-starter 自动配置
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Configuration
public class DatabaseConfig {

    // druid-spring-boot-3-starter 会根据 application.yml 中的配置自动创建数据源
    // 无需手动配置 DataSource Bean
    
    public DatabaseConfig() {
        log.info("数据库配置类初始化，Druid数据源将通过application.yml配置自动创建");
    }
}
