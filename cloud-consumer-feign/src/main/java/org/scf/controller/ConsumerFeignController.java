package org.scf.controller;

import org.scf.service.FeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerFeignController {

    private FeignService feignService;


    public ConsumerFeignController(FeignService feignService) {
        this.feignService = feignService;
    }

    @GetMapping("/consumer/feign/test/{message}")
    public String test(@PathVariable String message){
        return feignService.getProviderTest(message);
    }
}
