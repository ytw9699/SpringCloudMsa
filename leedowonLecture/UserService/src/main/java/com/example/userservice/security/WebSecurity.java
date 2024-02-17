package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration//우선순위 앞서서 빈등록함
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Environment env;
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 인증 관련 로직 처리, 인증이 되어야 권한부여 가능
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        //userService등록, 사용자가 보낸 유저네임과 패스워드를 가지고 로그인 처리
        //1. userDetailsService는 먼저 사용자 데이터를 검색한다 > select pwd from users where email = ?
        //2. passwordEncoder함수는 로그인시 입력한 비밀번호를 암호화(bCryptPasswordEncoder) 해서 디비에서 이미 암호화된 값과 비교한다.
        //db_pwd(encrypted) == input_pwd(encrypted)
    }

    @Override    // 권한 관련 로직 처리
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests()
                .antMatchers("/**")// 모든 요청
                .hasIpAddress("127.0.0.1")//이런 아이피만 통과, localhost라고 적으면 안됨
                .and()
                .addFilter(getAuthenticationFilter());//인증 필터 추가, 이 필터통과해야 권한부여

        http.headers().frameOptions().disable();//추가하지 않으면 h2 console 접근 안됨
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {

        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);
        //스프링 시큐리티에서 가져온 매니저 가지고 인증 처리. 즉 스프링 시큐리티 로그인 기능 이용하자
        return authenticationFilter;
    }
}
