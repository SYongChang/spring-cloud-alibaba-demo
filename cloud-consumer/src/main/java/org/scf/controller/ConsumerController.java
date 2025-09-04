package org.scf.controller;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    private RestTemplate restTemplate;


    public ConsumerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${service-url.nacos-provider-service}")
    private String serviceURL;


    @GetMapping("/consumer/test/{message}")
    public String test(@PathVariable("message") String message){
        String result = restTemplate.getForObject(serviceURL + "/provider/test/" + message, String.class);
        return "服务消费者调用返回--" + result;
    }
}
