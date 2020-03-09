package com.wkclz.core.config.balance;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;

// @Configuration
public class ConfigBean  {
    /*
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        //Ribbon 是客户端负载均衡的工具；
        return new RestTemplate();
    }
    */

    @Bean
    public IRule customeRoundRobinRule() {
        //自定义负载均衡规则
        return new CustomRoundRobinRule();
    }
}