package com.example.userservice2;

import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class UserService2Application {

    public static void main(String[] args) {
        SpringApplication.run(UserService2Application.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //사용자가 입력한 비밀번호 암호화 변환 작업

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate () {
        return new RestTemplate();
    }

    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

}
