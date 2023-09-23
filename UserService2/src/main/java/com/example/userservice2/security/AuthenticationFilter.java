package com.example.userservice2.security;

import com.example.userservice2.dto.UserDto;
import com.example.userservice2.service.UserService;
import com.example.userservice2.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * /login URL 요청 시 호출되는 custom Filter
 */
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {//로그인 요청 정보

        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())
                    //아이디, 패스워드, 권한들
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {
        String username = ((User) authResult.getPrincipal()).getUsername();

        log.debug(username);

        UserDto dto = userService.getUserDetailsByEmail(username);
        String time = env.getProperty("token.expiration_time");

        String token = Jwts.builder()
                            .setSubject(dto.getUserId())
                            .setExpiration(
                                    new Date(System.currentTimeMillis() +
                                    Long.parseLong(time)))//현재시간에서 + 24시간
                            .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))//암호화
                       .compact();

        response.addHeader("token", token);
        response.addHeader("userId", dto.getUserId());

        log.debug(token);
        log.debug(dto.getUserId());
    }
}
