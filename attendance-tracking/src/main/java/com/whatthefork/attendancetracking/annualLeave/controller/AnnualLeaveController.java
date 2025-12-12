package com.whatthefork.attendancetracking.annualLeave.controller;

import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveHistoryResponse;
import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveResponse;
import com.whatthefork.attendancetracking.annualLeave.dto.LeaveAnnualRequestDto;
import com.whatthefork.attendancetracking.annualLeave.service.AnnualLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.whatthefork.attendancetracking.common.ApiResponse;

import java.util.List;
import java.util.Optional;

import static com.whatthefork.attendancetracking.common.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/annualLeave")
public class AnnualLeaveController {

    private final AnnualLeaveService annualLeaveService;

    @Operation(summary = "연차 현황 조회", description = "연차 현황을 조회합니다.(년도별)")
    @GetMapping("/{year}")
    public ResponseEntity<ApiResponse> getAnnualLeaveSummary(
            @AuthenticationPrincipal String memberIds,
            @PathVariable Integer year
    ) {
        Long memberId =  Long.parseLong(memberIds);

        Optional<AnnualLeaveResponse> response = annualLeaveService.getAnnualLeave(memberId, year);
        return ResponseEntity.ok(success(response));
    }

    @Operation(summary = "연차 결재 히스토리 조회", description = "연차 결재 히스토리를 조회합니다.(년도별)")
    @GetMapping("/{year}/histories")
    public ResponseEntity<ApiResponse> getAnnualLeaveHistories(
            @AuthenticationPrincipal String memberIds,
            @PathVariable Integer year
    ) {
        Long memberId =  Long.parseLong(memberIds);

        List<AnnualLeaveHistoryResponse> responses =
                annualLeaveService.getAnnualLeaveHistories(memberId, year);
        return ResponseEntity.ok(success(responses));
    }

    @PostMapping("/decrease")
    public ResponseEntity<ApiResponse> decreaseAnnual(@RequestBody LeaveAnnualRequestDto requestDto){

        annualLeaveService.decreaseAnnual(requestDto);

        return ResponseEntity.ok(success("success"));
    }


}