package org.scf.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户登录日志表(UserLoginLog)实体类
 *
 * @author makejava
 * @since 2025-09-06 20:21:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_login_log")
public class UserLoginLog implements Serializable {
    private static final long serialVersionUID = 603029271635234158L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户openid
     */
    @TableField("openid")
    private String openid;

    /**
     * 登录类型：miniprogram-小程序，h5-H5，app-APP
     */
    @TableField("login_type")
    private String loginType;

    /**
     * 登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 登录地点
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 登录状态：0-失败，1-成功
     */
    @TableField("login_status")
    private Integer loginStatus;

    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}