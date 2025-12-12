package com.whatthefork.communicationandalarm.common.config;

import feign.RequestInterceptor;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            /* 현재 요청의 Http Servlet Request 를 가져옴 */
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (requestAttributes != null) {

                String authorizationHeader = requestAttributes
                        .getRequest()
                        .getHeader(HttpHeaders.AUTHORIZATION);


                if (authorizationHeader != null) {

                    requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationHeader);

                }
            }
        };
    }
}
