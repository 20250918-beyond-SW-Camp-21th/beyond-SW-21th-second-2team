package com.whatthefork.communicationandalarm.client;

import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.config.FeignClientConfig;
import com.whatthefork.communicationandalarm.common.dto.response.UserDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Member;

@FeignClient(name = "user-service", url="http://localhost:8000", configuration = FeignClientConfig.class)
public interface MemberClient {

    @GetMapping("/api/v1/user-service/users/{userId}")
    ApiResponse<UserDetailResponse> getUserDetail(@PathVariable String userId);
}
