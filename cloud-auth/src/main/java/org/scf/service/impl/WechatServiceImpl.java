package org.scf.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import lombok.extern.slf4j.Slf4j;
import org.scf.dto.wechat.WechatLoginResult;
import org.scf.service.WechatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 微信小程序服务实现类
 *
 * @author makejava
 * @since 2025-09-06 20:30:00
 */
@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    @Value("${miniprogram.appid}")
    private String appid;

    @Value("${miniprogram.secret}")
    private String secret;

    private static final String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public WechatLoginResult login(String code) {
        log.info("开始微信小程序登录，code: {}", code);
        try {
            // 构建请求URL
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    WECHAT_LOGIN_URL, appid, secret, code);
            log.debug("微信登录请求URL: {}", url);

            // 发送HTTP请求
            String response = HttpUtil.get(url);
            log.info("微信登录响应: {}", response);

            // 解析响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            WechatLoginResult result = new WechatLoginResult();
            
            if (jsonObject.containsKey("errcode")) {
                result.setErrcode(jsonObject.getInt("errcode"));
                result.setErrmsg(jsonObject.getStr("errmsg"));
                log.warn("微信登录失败，错误码: {}, 错误信息: {}", result.getErrcode(), result.getErrmsg());
            } else {
                result.setOpenid(jsonObject.getStr("openid"));
                result.setSessionKey(jsonObject.getStr("session_key"));
                result.setUnionid(jsonObject.getStr("unionid"));
                log.info("微信登录成功，openid: {}, unionid: {}", result.getOpenid(), result.getUnionid());
            }

            return result;
        } catch (Exception e) {
            log.error("微信小程序登录失败，code: {}", code, e);
            WechatLoginResult result = new WechatLoginResult();
            result.setErrcode(-1);
            result.setErrmsg("微信登录服务异常");
            return result;
        }
    }

    @Override
    public String decryptUserInfo(String encryptedData, String iv, String sessionKey) {
        log.info("开始解密微信用户信息，encryptedData长度: {}, iv长度: {}", 
                encryptedData != null ? encryptedData.length() : 0, 
                iv != null ? iv.length() : 0);
        try {
            // 使用AES解密 - 微信小程序使用AES-128-CBC模式
            AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, 
                             sessionKey.getBytes(StandardCharsets.UTF_8), 
                             iv.getBytes(StandardCharsets.UTF_8));
            String decryptedData = aes.decryptStr(encryptedData);
            log.info("微信用户信息解密成功，解密后数据长度: {}", decryptedData != null ? decryptedData.length() : 0);
            return decryptedData;
        } catch (Exception e) {
            log.error("解密微信用户信息失败，encryptedData: {}, iv: {}", encryptedData, iv, e);
            return null;
        }
    }
}
