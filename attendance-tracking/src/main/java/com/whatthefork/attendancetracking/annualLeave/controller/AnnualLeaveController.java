package com.whatthefork.attendancetracking.annualLeave.controller;

import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveHistoryResponse;
import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveResponse;
import com.whatthefork.attendancetracking.annualLeave.service.AnnualLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.whatthefork.attendancetracking.common.ApiResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AnnualLeaveController {

    private final AnnualLeaveService annualLeaveService;

    //연차 결재 현황 확인(년도별)
    @GetMapping("/{memberId}/{year}")
    public ResponseEntity<ApiResponse> getAnnualLeaveSummary(
            @PathVariable Long memberId,
            @PathVariable Integer year
    ) {
        Optional<AnnualLeaveResponse> response = annualLeaveService.getAnnualLeave(memberId, year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    //연차 결재 히스토리 조회
    @GetMapping("/{memberId}/{year}/histories")
    public ResponseEntity<ApiResponse> getAnnualLeaveHistories(
            @PathVariable Long memberId,
            @PathVariable Integer year
    ) {
        List<AnnualLeaveHistoryResponse> responses =
                annualLeaveService.getAnnualLeaveHistories(memberId, year);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }


}