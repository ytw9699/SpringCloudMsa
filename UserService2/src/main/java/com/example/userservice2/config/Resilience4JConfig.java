package com.example.userservice2.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class Resilience4JConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                    .failureRateThreshold(4)//실패 비율이 4% 이상일 경우 서킷브레이커 온
                    .waitDurationInOpenState(Duration.ofMillis(1000))//서킷브레이커 온 상태 1초 유지 후 닫음
                    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                    //가장 최근의 2개 요청에 대한 성공과 실패를 토대로 비율을 추적한다. 1번 실패시 실패비율 50프로이니까 바로 서킷 브레이커 켜진다.
                    .slidingWindowSize(2)//2번의 카운트 설정
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                                                .timeoutDuration(Duration.ofSeconds(4))//4초이상 응답 없으면 온시킨다
                                            .build();

        return factory -> factory.configureDefault(
                                id -> new Resilience4JConfigBuilder(id)
                                        .timeLimiterConfig(timeLimiterConfig)
                                        .circuitBreakerConfig(circuitBreakerConfig)
                        .build()
        );
    }
}