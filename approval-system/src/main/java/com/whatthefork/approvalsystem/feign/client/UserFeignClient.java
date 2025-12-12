package com.whatthefork.approvalsystem.feign.client;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.common.config.FeignConfig;
import com.whatthefork.approvalsystem.feign.dto.UserDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userservice", url="http://localhost:8000", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/v1/user-service/users/{userId}")
    ApiResponse<UserDetailResponse> findUserDetail(@PathVariable("userId") Long memberId);
}

