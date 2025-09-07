package org.scf.config;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT配置类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 配置JWT签名密钥
     */
    @Bean
    public SecretKey jwtSecretKey() {
        try {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            
            // 验证密钥长度
            if (keyBytes.length < 64) {
                log.warn("JWT密钥长度不足，当前长度: {} 字节，HS512算法需要至少64字节(512位)", keyBytes.length);
                log.warn("建议使用更长的密钥，或者使用Keys.secretKeyFor(SignatureAlgorithm.HS512)生成安全密钥");
                
                // 生成一个安全的密钥作为备选
                SecretKey secureKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
                log.info("已生成安全的HS512密钥作为备选");
                return secureKey;
            }
            
            log.info("JWT密钥配置成功，密钥长度: {} 字节", keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
            
        } catch (Exception e) {
            log.error("JWT密钥配置失败，使用默认安全密钥", e);
            return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        }
    }
}
