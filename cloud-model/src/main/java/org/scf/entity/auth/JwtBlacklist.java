package org.scf.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JWT Token黑名单表(JwtBlacklist)实体类
 *
 * @author makejava
 * @since 2025-09-06 20:22:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("jwt_blacklist")
public class JwtBlacklist implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * JWT Token唯一标识
     */
    @TableField("token_id")
    private String tokenId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * Token类型：access-访问令牌，refresh-刷新令牌
     */
    @TableField("token_type")
    private String tokenType;

    /**
     * Token过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 加入黑名单原因：logout-主动登出，security-安全原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
