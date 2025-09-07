package org.scf.config;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置表(SystemConfig)实体类
 *
 * @author makejava
 * @since 2025-09-06 20:20:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_config")
public class SystemConfig implements Serializable {
    private static final long serialVersionUID = -15327214446446320L;

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置类型：string-字符串，number-数字，boolean-布尔，json-JSON
     */
    @TableField("config_type")
    private String configType;

    /**
     * 配置分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否敏感信息：0-否，1-是
     */
    @TableField("is_sensitive")
    private Integer isSensitive;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}