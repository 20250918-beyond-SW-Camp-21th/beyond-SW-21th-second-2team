package com.whatthefork.approvalsystem.feign.client;

import com.whatthefork.approvalsystem.feign.dto.LeaveAnnualRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "attendance-tracking")
public interface AnnualLeaveFeignClient {

    @PostMapping("/api/v1/attendance-tracking/annual-leave/decrease")
    ResponseEntity<Void> decreaseAnnualLeave(@RequestBody LeaveAnnualRequestDto requestDto);
}
