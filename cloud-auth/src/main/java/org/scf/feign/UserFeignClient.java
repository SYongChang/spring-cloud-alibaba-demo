package org.scf.feign;

import org.scf.dto.common.ApiResponse;
import org.scf.dto.user.UserUpdateRequest;
import org.scf.entity.user.MiniprogramUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务Feign客户端
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@FeignClient(name = "user-service", path = "/user")
public interface UserFeignClient {

    /**
     * 根据openid获取用户信息
     */
    @GetMapping("/info/openid/{openid}")
    ApiResponse<MiniprogramUser> getUserByOpenid(@PathVariable("openid") String openid);

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/info/{userId}")
    ApiResponse<MiniprogramUser> getUserById(@PathVariable("userId") Long userId);

    /**
     * 创建新用户
     */
    @PostMapping("/create")
    ApiResponse<MiniprogramUser> createUser(@RequestBody MiniprogramUser user);

    /**
     * 更新用户信息
     */
    @PutMapping("/info/{userId}")
    ApiResponse<Boolean> updateUserInfo(@PathVariable("userId") Long userId, 
                                        @RequestBody UserUpdateRequest updateRequest);

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check/phone")
    ApiResponse<Boolean> checkPhoneExists(@RequestParam("phone") String phone, 
                                         @RequestParam(value = "excludeUserId", required = false) Long excludeUserId);

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email")
    ApiResponse<Boolean> checkEmailExists(@RequestParam("email") String email, 
                                         @RequestParam(value = "excludeUserId", required = false) Long excludeUserId);

    /**
     * 更新用户最后登录信息
     */
    @PutMapping("/login-info/{userId}")
    ApiResponse<Boolean> updateLastLoginInfo(@PathVariable("userId") Long userId, 
                                            @RequestParam("loginIp") String loginIp);

    /**
     * 增加用户登录次数
     */
    @PutMapping("/login-count/{userId}")
    ApiResponse<Boolean> incrementLoginCount(@PathVariable("userId") Long userId);
}
