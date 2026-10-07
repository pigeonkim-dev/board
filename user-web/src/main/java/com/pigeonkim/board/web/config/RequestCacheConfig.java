package com.pigeonkim.board.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * "원래 가려던 요청" 저장소. SecurityConfig 밖에 둔 이유:
 * SecurityConfig 는 SignupRequiredAccessDeniedHandler 를 필요로 하고, 그 핸들러는 RequestCache 를 필요로 한다.
 * RequestCache 빈이 SecurityConfig 안에 있으면 "SecurityConfig 를 만들려면 SecurityConfig 가 먼저 있어야 한다" 는 고리가 생긴다.
 */
@Configuration
public class RequestCacheConfig {

    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }

}
