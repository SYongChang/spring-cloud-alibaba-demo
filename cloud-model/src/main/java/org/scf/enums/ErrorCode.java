package org.scf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // 通用错误码
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_ERROR(500, "内部服务器错误"),
    
    // 认证相关错误码
    LOGIN_FAILED(1001, "登录失败"),
    TOKEN_INVALID(1002, "Token无效"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    TOKEN_BLACKLISTED(1004, "Token已被拉黑"),
    REFRESH_TOKEN_INVALID(1005, "刷新Token无效"),
    REFRESH_TOKEN_EXPIRED(1006, "刷新Token已过期"),
    USER_NOT_FOUND(1007, "用户不存在"),
    USER_DISABLED(1008, "用户已被禁用"),
    USER_FROZEN(1009, "用户已被冻结"),
    
    // 微信小程序相关错误码
    WECHAT_LOGIN_FAILED(2001, "微信登录失败"),
    WECHAT_CODE_INVALID(2002, "微信登录凭证无效"),
    WECHAT_SESSION_EXPIRED(2003, "微信会话已过期"),
    WECHAT_USER_INFO_DECRYPT_FAILED(2004, "微信用户信息解密失败"),
    
    // 用户相关错误码
    USER_PHONE_EXISTS(3001, "手机号已存在"),
    USER_EMAIL_EXISTS(3002, "邮箱已存在"),
    USER_UPDATE_FAILED(3003, "用户信息更新失败"),
    USER_CREATE_FAILED(3004, "用户创建失败"),
    
    // 系统配置相关错误码
    CONFIG_NOT_FOUND(4001, "配置不存在"),
    CONFIG_VALUE_INVALID(4002, "配置值无效"),
    
    // 数据库相关错误码
    DATABASE_ERROR(5001, "数据库操作失败"),
    DATA_NOT_FOUND(5002, "数据不存在"),
    DATA_ALREADY_EXISTS(5003, "数据已存在"),
    
    // 缓存相关错误码
    CACHE_ERROR(6001, "缓存操作失败"),
    CACHE_KEY_NOT_FOUND(6002, "缓存键不存在"),
    
    // 网络相关错误码
    NETWORK_ERROR(7001, "网络请求失败"),
    TIMEOUT_ERROR(7002, "请求超时"),
    
    // 文件相关错误码
    FILE_UPLOAD_FAILED(8001, "文件上传失败"),
    FILE_NOT_FOUND(8002, "文件不存在"),
    FILE_TYPE_NOT_SUPPORTED(8003, "文件类型不支持"),
    
    // 权限相关错误码
    PERMISSION_DENIED(9001, "权限不足"),
    ROLE_NOT_FOUND(9002, "角色不存在"),
    PERMISSION_NOT_FOUND(9003, "权限不存在");

    private final Integer code;
    private final String message;
}
