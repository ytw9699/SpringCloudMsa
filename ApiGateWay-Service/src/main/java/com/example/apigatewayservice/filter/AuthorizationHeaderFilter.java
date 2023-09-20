package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
            //권한? 관련

    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    // login -> token -> users (with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {

            log.info("AuthorizationHeaderFilter");

            // 사용자가 API에 요청을 했을 때 같이 보낸 token 을 확인
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                 return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            if (!isJwtValid(jwt)) {//정상 토근인지 디코드 확인
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {

        boolean returnBool = true;
        String subject = null;

        try {// 토큰 검증
             subject = Jwts.parser()
                            .setSigningKey(env.getProperty("token.secret"))//복호화
                            .parseClaimsJws(jwt)//복호화 대상
                       .getBody()
                       .getSubject();//아이디값 가져와본다. 그리고 기존 유저 아이디와 비교
             
        } catch (Exception e) {//파싱하다 에러시
            returnBool = false;
        }

        if (subject == null || subject.isEmpty())//null 이거나 비어있다면
            returnBool = false;

        return returnBool;
    }

    // Mono, Flux -> Spring WebFlux
    // 단일 값 return -> Mono , 다중 값 return -> Flux
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
                           response.setStatusCode(httpStatus);
        log.error(err);

        return response.setComplete();//Mono타입 반환
    }

    public static class Config {}
}
