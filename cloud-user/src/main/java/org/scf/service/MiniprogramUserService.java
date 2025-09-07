package org.scf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scf.dto.user.UserUpdateRequest;
import org.scf.entity.user.MiniprogramUser;
import org.springframework.stereotype.Service;

/**
 * 小程序用户服务接口
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
public interface MiniprogramUserService extends IService<MiniprogramUser> {

    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    MiniprogramUser getByOpenid(String openid);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    MiniprogramUser getByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    MiniprogramUser getByEmail(String email);

    /**
     * 创建新用户
     *
     * @param user 用户信息
     * @return 创建的用户信息
     */
    MiniprogramUser createUser(MiniprogramUser user);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param updateRequest 更新请求
     * @return 是否更新成功
     */
    boolean updateUserInfo(Long userId, UserUpdateRequest updateRequest);

    /**
     * 更新用户最后登录信息
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @return 是否更新成功
     */
    boolean updateLastLoginInfo(Long userId, String loginIp);

    /**
     * 增加用户登录次数
     *
     * @param userId 用户ID
     * @return 是否更新成功
     */
    boolean incrementLoginCount(Long userId);

    /**
     * 检查手机号是否已存在
     *
     * @param phone 手机号
     * @param excludeUserId 排除的用户ID
     * @return 是否存在
     */
    boolean isPhoneExists(String phone, Long excludeUserId);

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @return 是否存在
     */
    boolean isEmailExists(String email, Long excludeUserId);
}
