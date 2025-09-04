package org.scf.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "cloud-provider")
public interface FeignService {

    @GetMapping("/provider/test/{message}")
    public String getProviderTest(@PathVariable("message") String message);
}
