package com.example.userservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration//우선순위 앞서서 빈등록함
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override    // 권한 관련 로직 처리
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.headers().frameOptions().disable();//추가하지 않으면 h2 console 접근 안됨
    }
}
