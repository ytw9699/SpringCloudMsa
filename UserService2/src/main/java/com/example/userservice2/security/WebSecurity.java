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
        //.antMatchers("/error/**").permitAll()
        //http.authorizeRequests().antMatchers("/userList").permitAll();
        //http.authorizeRequests().antMatchers("/join").permitAll();

        http.authorizeRequests()
                .antMatchers("/**")// 모든 요청
                .hasIpAddress("127.0.0.1")//이런 아이피만 통과 .hasIpaddress(IP) -> .access("hasIpAddress('" + IP + "')")
                .and()
                .addFilter(getAuthenticationFilter());//인증 필터 추가, 인증시 필터 걸림
        //https://www.inflearn.com/questions/423204/%EB%B9%84%EA%B5%90%EC%A0%81-%EC%B5%9C%EC%8B%A0-%EB%B2%84%EC%A0%84%EC%97%90%EC%84%9C-%EC%97%90%EB%9F%AC%EA%B0%80-%EB%B0%9C%EC%83%9D%ED%95%A9%EB%8B%88%EB%8B%A4
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {

        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);
                //스프링 시큐리티에서 가져온 매니저 가지고 인증 처리

        return authenticationFilter;
    }
}
