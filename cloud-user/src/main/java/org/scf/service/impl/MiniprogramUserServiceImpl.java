package org.scf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.scf.dto.user.UserUpdateRequest;
import org.scf.entity.user.MiniprogramUser;
import org.scf.mapper.MiniprogramUserMapper;
import org.scf.service.MiniprogramUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 小程序用户服务实现类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Service
public class MiniprogramUserServiceImpl extends ServiceImpl<MiniprogramUserMapper, MiniprogramUser> implements MiniprogramUserService {

    @Override
    public MiniprogramUser getByOpenid(String openid) {
        return baseMapper.selectByOpenid(openid);
    }

    @Override
    public MiniprogramUser getByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }

    @Override
    public MiniprogramUser getByEmail(String email) {
        return baseMapper.selectByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniprogramUser createUser(MiniprogramUser user) {
        try {
            log.info("开始创建新用户，openid: {}", user.getOpenid());
            
            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus(1); // 正常状态
            }
            if (user.getLoginCount() == null) {
                user.setLoginCount(0);
            }
            if (user.getDeleted() == null) {
                user.setDeleted(0);
            }
            if (user.getCreateTime() == null) {
                user.setCreateTime(LocalDateTime.now());
            }
            if (user.getUpdateTime() == null) {
                user.setUpdateTime(LocalDateTime.now());
            }
            
            // 保存用户
            boolean saved = save(user);
            if (saved && user.getId() != null) {
                log.info("新用户创建成功，userId: {}, openid: {}", user.getId(), user.getOpenid());
                return user;
            } else {
                log.error("新用户创建失败，openid: {}", user.getOpenid());
                return null;
            }
        } catch (Exception e) {
            log.error("创建用户失败，openid: {}", user.getOpenid(), e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(Long userId, UserUpdateRequest updateRequest) {
        try {
            MiniprogramUser user = getById(userId);
            if (user == null) {
                log.warn("用户不存在，userId: {}", userId);
                return false;
            }

            // 检查手机号是否重复
            if (updateRequest.getPhone() != null && !updateRequest.getPhone().equals(user.getPhone())) {
                if (isPhoneExists(updateRequest.getPhone(), userId)) {
                    log.warn("手机号已存在，phone: {}", updateRequest.getPhone());
                    return false;
                }
            }

            // 检查邮箱是否重复
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
                if (isEmailExists(updateRequest.getEmail(), userId)) {
                    log.warn("邮箱已存在，email: {}", updateRequest.getEmail());
                    return false;
                }
            }

            // 更新用户信息
            BeanUtils.copyProperties(updateRequest, user);
            user.setUpdateTime(LocalDateTime.now());
            
            return updateById(user);
        } catch (Exception e) {
            log.error("更新用户信息失败，userId: {}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLastLoginInfo(Long userId, String loginIp) {
        try {
            MiniprogramUser user = getById(userId);
            if (user == null) {
                log.warn("用户不存在，userId: {}", userId);
                return false;
            }

            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(loginIp);
            user.setUpdateTime(LocalDateTime.now());
            
            return updateById(user);
        } catch (Exception e) {
            log.error("更新用户最后登录信息失败，userId: {}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementLoginCount(Long userId) {
        try {
            int result = baseMapper.incrementLoginCount(userId);
            return result > 0;
        } catch (Exception e) {
            log.error("增加用户登录次数失败，userId: {}", userId, e);
            return false;
        }
    }

    @Override
    public boolean isPhoneExists(String phone, Long excludeUserId) {
        LambdaQueryWrapper<MiniprogramUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MiniprogramUser::getPhone, phone)
               .eq(MiniprogramUser::getDeleted, 0);
        
        if (excludeUserId != null) {
            wrapper.ne(MiniprogramUser::getId, excludeUserId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public boolean isEmailExists(String email, Long excludeUserId) {
        LambdaQueryWrapper<MiniprogramUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MiniprogramUser::getEmail, email)
               .eq(MiniprogramUser::getDeleted, 0);
        
        if (excludeUserId != null) {
            wrapper.ne(MiniprogramUser::getId, excludeUserId);
        }
        
        return count(wrapper) > 0;
    }
}
