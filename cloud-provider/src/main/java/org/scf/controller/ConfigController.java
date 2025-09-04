package org.scf.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigController {

    @Value("${test.config}")
    private String testConfig;

    @GetMapping(value = "/provider/config")
    public String getConfig(){
        return "服务提供者读取配置信息：" + testConfig;
    }
}
