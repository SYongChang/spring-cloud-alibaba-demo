package org.scf.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 开始访问时间
     */
    private static final String BEGIN_REQUEST_TIME = "begin_request_time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 记录请求接口的开始时间
        exchange.getAttributes().put(BEGIN_REQUEST_TIME, System.currentTimeMillis());

        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            Long beginRequestTime = exchange.getAttribute(BEGIN_REQUEST_TIME);
            if (beginRequestTime != null){
                log.info("请求接口IP: " + exchange.getRequest().getURI().getHost());
                log.info("请求接口PORT: " + exchange.getRequest().getURI().getPort());
                log.info("请求接口URL: " + exchange.getRequest().getURI().getPath());
                log.info("请求接口URL参数: " + exchange.getRequest().getURI().getRawQuery());
                log.info("请求接口时长: " + (System.currentTimeMillis() - beginRequestTime) + "ms");
            }
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
