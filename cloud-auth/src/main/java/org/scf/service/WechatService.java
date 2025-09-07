package org.scf.service;

import org.scf.dto.wechat.WechatLoginResult;

/**
 * 微信小程序服务接口
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
public interface WechatService {

    /**
     * 微信小程序登录
     *
     * @param code 微信登录凭证
     * @return 登录结果
     */
    WechatLoginResult login(String code);

    /**
     * 解密微信用户信息
     *
     * @param encryptedData 加密数据
     * @param iv 初始向量
     * @param sessionKey 会话密钥
     * @return 解密后的用户信息
     */
    String decryptUserInfo(String encryptedData, String iv, String sessionKey);
}
