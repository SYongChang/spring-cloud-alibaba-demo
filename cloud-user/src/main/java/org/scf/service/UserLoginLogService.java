package org.scf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scf.entity.user.UserLoginLog;
import org.springframework.stereotype.Service;

/**
 * 用户登录日志服务接口
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
public interface UserLoginLogService extends IService<UserLoginLog> {

    /**
     * 记录用户登录日志
     *
     * @param userId 用户ID
     * @param openid 用户openid
     * @param loginType 登录类型
     * @param loginIp 登录IP
     * @param loginLocation 登录地点
     * @param userAgent 用户代理
     * @param deviceInfo 设备信息
     * @param loginStatus 登录状态
     * @param failReason 失败原因
     * @param sessionId 会话ID
     * @return 是否记录成功
     */
    boolean recordLoginLog(Long userId, String openid, String loginType, String loginIp, 
                          String loginLocation, String userAgent, String deviceInfo, 
                          Integer loginStatus, String failReason, String sessionId);
}
