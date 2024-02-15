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
        //http.authorizeRequests().antMatchers("/users/**").permitAll();

        http.authorizeRequests()
                .antMatchers("/**")// 모든 요청
                .hasIpAddress("127.0.0.1")//이런 아이피만 통과
                .and()
                .addFilter(getAuthenticationFilter());//인증 필터 추가, 이 필터통과해야 권한부여

        http.headers().frameOptions().disable();//추가하지 않으면 h2 console 접근 안됨
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
                             authenticationFilter.setAuthenticationManager(authenticationManager());
        //스프링 시큐리티에서 가져온 매니저 가지고 인증 처리. 즉 스프링 시큐리티 로그인 기능 이용하자
        return authenticationFilter;
    }
}
