package org.scf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.scf.entity.user.UserLoginLog;
import org.scf.mapper.UserLoginLogMapper;
import org.scf.service.UserLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户登录日志服务实现类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Service
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, UserLoginLog> implements UserLoginLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordLoginLog(Long userId, String openid, String loginType, String loginIp, 
                                 String loginLocation, String userAgent, String deviceInfo, 
                                 Integer loginStatus, String failReason, String sessionId) {
        try {
            UserLoginLog loginLog = new UserLoginLog();
            loginLog.setUserId(userId);
            loginLog.setOpenid(openid);
            loginLog.setLoginType(loginType);
            loginLog.setLoginIp(loginIp);
            loginLog.setLoginLocation(loginLocation);
            loginLog.setUserAgent(userAgent);
            loginLog.setDeviceInfo(deviceInfo);
            loginLog.setLoginStatus(loginStatus);
            loginLog.setFailReason(failReason);
            loginLog.setSessionId(sessionId);
            loginLog.setCreateTime(LocalDateTime.now());
            
            return save(loginLog);
        } catch (Exception e) {
            log.error("记录用户登录日志失败，userId: {}, openid: {}", userId, openid, e);
            return false;
        }
    }
}
