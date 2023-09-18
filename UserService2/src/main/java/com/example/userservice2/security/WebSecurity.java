package com.example.userservice2.security;

import com.example.userservice2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 인증 관련 로직 처리, 인증이 되어야 권한부여 가능
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        //userService등록, 사용자가 보낸 유저네임과 패스워드를 가지고 로그인 처리
        //1. 먼저 사용자 데이터를 검색한다 > select pwd from users where email = ?
        //2. 로그인시 입력한 비밀번호를 암호화(bCryptPasswordEncoder) 해서 디비에서 이미 암호화된 값과 비교한다.
    }

    // 권한 관련 로직 처리
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/**").permitAll();//모든 것 통과
        //http.authorizeRequests().antMatchers("/users").permitAll();
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.headers().frameOptions().disable();
        //http.authorizeRequests().antMatchers("/userList").permitAll();
        //http.authorizeRequests().antMatchers("/join").permitAll();

        http.authorizeRequests()
                .antMatchers("/**")
                .hasIpAddress("127.0.0.1")//이런 아이피만 통과
                .and()
                .addFilter(getAuthenticationFilter());//모든 요청에 대해 인증 필터 작업 처리
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {

        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);
                //스프링 시큐리티에서 가져온 매니저 가지고 인증 처리

        return authenticationFilter;
    }
}
