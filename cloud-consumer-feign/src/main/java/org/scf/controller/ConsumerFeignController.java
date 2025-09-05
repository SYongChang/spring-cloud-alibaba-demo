package org.scf.controller;

import lombok.extern.slf4j.Slf4j;
import org.scf.service.FeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ConsumerFeignController {

    private FeignService feignService;


    public ConsumerFeignController(FeignService feignService) {
        this.feignService = feignService;
    }

    @GetMapping("/consumer/feign/test/{message}")
    public String test(@PathVariable String message){
        long start = System.currentTimeMillis();
        log.info("调用provider开始，{}",start);
        String result = null;
        try {
            result = feignService.getProviderTest(message);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            log.info("调用provider结束，{}ms",System.currentTimeMillis()-start);
        }

        return result;
    }
}
