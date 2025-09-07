package org.scf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.scf.entity.user.UserLoginLog;

/**
 * 用户登录日志Mapper接口
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
}
