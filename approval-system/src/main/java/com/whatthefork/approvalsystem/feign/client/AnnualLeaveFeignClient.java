package com.whatthefork.approvalsystem.feign.client;

import com.whatthefork.approvalsystem.common.config.FeignConfig;
import com.whatthefork.approvalsystem.feign.dto.LeaveAnnualRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "attendancetracking", url="http://localhost:8000", configuration = FeignConfig.class)
public interface AnnualLeaveFeignClient {

    @PostMapping("/api/v1/attendance-tracking/annualLeave/decrease")
    ResponseEntity<Void> decreaseAnnualLeave(LeaveAnnualRequestDto requestDto);
}
