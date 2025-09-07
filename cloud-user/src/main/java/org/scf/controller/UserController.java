package org.scf.controller;

import lombok.extern.slf4j.Slf4j;
import org.scf.dto.common.ApiResponse;
import org.scf.dto.user.UserUpdateRequest;
import org.scf.entity.user.MiniprogramUser;
import org.scf.service.MiniprogramUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户管理控制器
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MiniprogramUserService userService;

    /**
     * 根据openid获取用户信息
     */
    @GetMapping("/info/openid/{openid}")
    public ApiResponse<MiniprogramUser> getUserByOpenid(@PathVariable String openid) {
        try {
            MiniprogramUser user = userService.getByOpenid(openid);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("根据openid获取用户信息失败，openid: {}", openid, e);
            return ApiResponse.error("获取用户信息失败");
        }
    }

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/info/{userId}")
    public ApiResponse<MiniprogramUser> getUserById(@PathVariable Long userId) {
        try {
            MiniprogramUser user = userService.getById(userId);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("根据用户ID获取用户信息失败，userId: {}", userId, e);
            return ApiResponse.error("获取用户信息失败");
        }
    }

    /**
     * 创建新用户
     */
    @PostMapping("/create")
    public ApiResponse<MiniprogramUser> createUser(@Valid @RequestBody MiniprogramUser user) {
        try {
            log.info("创建新用户，openid: {}", user.getOpenid());
            
            // 检查openid是否已存在
            if (userService.getByOpenid(user.getOpenid()) != null) {
                log.warn("用户已存在，openid: {}", user.getOpenid());
                return ApiResponse.error(400, "用户已存在");
            }
            
            // 创建用户
            MiniprogramUser createdUser = userService.createUser(user);
            if (createdUser != null && createdUser.getId() != null) {
                log.info("新用户创建成功，userId: {}, openid: {}", createdUser.getId(), createdUser.getOpenid());
                return ApiResponse.success(createdUser);
            } else {
                log.error("新用户创建失败，openid: {}", user.getOpenid());
                return ApiResponse.error("用户创建失败");
            }
        } catch (Exception e) {
            log.error("创建新用户失败，openid: {}", user.getOpenid(), e);
            return ApiResponse.error("用户创建失败");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info/{userId}")
    public ApiResponse<Boolean> updateUserInfo(@PathVariable Long userId, 
                                              @Valid @RequestBody UserUpdateRequest updateRequest) {
        try {
            boolean result = userService.updateUserInfo(userId, updateRequest);
            if (result) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error("用户信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败，userId: {}", userId, e);
            return ApiResponse.error("更新用户信息失败");
        }
    }

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check/phone")
    public ApiResponse<Boolean> checkPhoneExists(@RequestParam String phone, 
                                                @RequestParam(required = false) Long excludeUserId) {
        try {
            boolean exists = userService.isPhoneExists(phone, excludeUserId);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            log.error("检查手机号是否存在失败，phone: {}", phone, e);
            return ApiResponse.error("检查手机号失败");
        }
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email")
    public ApiResponse<Boolean> checkEmailExists(@RequestParam String email, 
                                                @RequestParam(required = false) Long excludeUserId) {
        try {
            boolean exists = userService.isEmailExists(email, excludeUserId);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            log.error("检查邮箱是否存在失败，email: {}", email, e);
            return ApiResponse.error("检查邮箱失败");
        }
    }

    /**
     * 更新用户最后登录信息
     */
    @PutMapping("/login-info/{userId}")
    public ApiResponse<Boolean> updateLastLoginInfo(@PathVariable Long userId, 
                                                   @RequestParam String loginIp) {
        try {
            boolean result = userService.updateLastLoginInfo(userId, loginIp);
            if (result) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error("登录信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户最后登录信息失败，userId: {}", userId, e);
            return ApiResponse.error("更新登录信息失败");
        }
    }

    /**
     * 增加用户登录次数
     */
    @PutMapping("/login-count/{userId}")
    public ApiResponse<Boolean> incrementLoginCount(@PathVariable Long userId) {
        try {
            boolean result = userService.incrementLoginCount(userId);
            if (result) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error("登录次数更新失败");
            }
        } catch (Exception e) {
            log.error("增加用户登录次数失败，userId: {}", userId, e);
            return ApiResponse.error("更新登录次数失败");
        }
    }
}
