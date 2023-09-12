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

    private Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 인증 관련 로직 처리
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 로그인 시 비밀번호를 암호화
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    // 권한 관련 로직 처리
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/users").permitAll();
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.authorizeRequests().antMatchers("/userList").permitAll();
        http.authorizeRequests().antMatchers("/join").permitAll();

        http.authorizeRequests()
                .antMatchers("/**")
                .access("hasIpAddress('220.86.33.84')")
                .and()
                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);

        return authenticationFilter;
    }


}
