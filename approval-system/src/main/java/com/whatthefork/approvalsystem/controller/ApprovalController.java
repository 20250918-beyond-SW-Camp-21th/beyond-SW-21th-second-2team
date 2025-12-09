package com.whatthefork.approvalsystem.controller;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PutMapping("/{docId}/submit")
    public ResponseEntity<ApiResponse> submitApproval(/*Long memberId,*/ @PathVariable Long docId) {
        Long memberId = 101L;
        approvalService.submitApproval(docId, memberId);
        return ResponseEntity.ok(ApiResponse.success("상신 완료"));
    }

    @PutMapping("/{docId}/submit/cancel")
    public ResponseEntity<ApiResponse> cancelApproval(@PathVariable Long docId) {
        Long memberId = 101L;
        approvalService.cancleSubmit(docId, memberId);
        return ResponseEntity.ok(ApiResponse.success("상신 취소 완료"));
    }
}
