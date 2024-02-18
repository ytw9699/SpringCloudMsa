package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
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
            //post 요청이라 getInputStream으로 받아 처리

            return getAuthenticationManager().authenticate(//인증 처리해주는 Manager에 넘겨서 아이디 패스워드 비교
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())
                    //인증 정보 전달 인자값 전달: 1.아이디, 2.패스워드, 3.권한들
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override//로그인 성공했을때 어떠한 처리할것인지? 토큰발행
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String username = ((User) authResult.getPrincipal()).getUsername();

        log.debug(username);

        UserDto dto = userService.getUserDetailsByEmail(username);
        String time = env.getProperty("token.expiration_time");

        String token = Jwts.builder()
                                .setSubject(dto.getUserId())
                                .setExpiration(
                                        new Date(System.currentTimeMillis() +
                                                Long.parseLong(time)))//현재시간에서 + 24시간
                                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))//키조합하여 암호화
                           .compact();//토큰 스트링 생성

        response.addHeader("token", token);
        response.addHeader("userId", dto.getUserId());//정상인지 다시 확인

        log.debug(token);
        log.debug(dto.getUserId());
    }
}
