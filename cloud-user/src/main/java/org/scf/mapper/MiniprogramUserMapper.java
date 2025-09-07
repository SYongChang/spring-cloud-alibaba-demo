package org.scf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scf.entity.user.MiniprogramUser;

/**
 * 小程序用户Mapper接口
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Mapper
public interface MiniprogramUserMapper extends BaseMapper<MiniprogramUser> {

    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    MiniprogramUser selectByOpenid(@Param("openid") String openid);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    MiniprogramUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    MiniprogramUser selectByEmail(@Param("email") String email);

    /**
     * 更新用户最后登录信息
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @return 更新行数
     */
    int updateLastLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp);

    /**
     * 增加用户登录次数
     *
     * @param userId 用户ID
     * @return 更新行数
     */
    int incrementLoginCount(@Param("userId") Long userId);
}
