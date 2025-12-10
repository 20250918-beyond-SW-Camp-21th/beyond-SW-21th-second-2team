package com.whatthefork.approvalsystem.controller;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Approval", description = "결재 처리 API (상신, 승인, 반려, 취소)")
@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @Operation(summary = "결재 상신", description = "작성한 기안 문서를 결재 라인에 태웁니다(상신). 상태가 '진행 중'으로 변경됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음(본인이 작성한 문서가 아님) [A001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 [D001]"
            )
    })
    @PutMapping("/{docId}/submit")
    public ResponseEntity<ApiResponse> submitApproval(
            @AuthenticationPrincipal String memberId,
            @PathVariable Long docId) {
        approvalService.submitApproval(docId, Long.valueOf(memberId));
        return ResponseEntity.ok(ApiResponse.success("상신 완료"));
    }

    @Operation(summary = "상신 취소", description = "상신한 문서를 회수합니다. 첫 번째 결재자가 아직 읽지 않았을 때만 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "취소 불가(결재자가 이미 문서를 읽었거나 결재함) [P001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음(본인이 작성한 문서가 아님) [A001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 / 결재정보 없음 [D001, D004]"
            )
    })
    @PutMapping("/{docId}/submit/cancel")
    public ResponseEntity<ApiResponse> cancelApproval(
            @AuthenticationPrincipal String memberId,
            @PathVariable Long docId) {
        approvalService.cancelSubmit(docId, Long.valueOf(memberId));
        return ResponseEntity.ok(ApiResponse.success("상신 취소 완료"));
    }

    @Operation(summary = "결재 승인", description = "문서를 승인합니다. 다음 결재자에게 넘어가거나, 최종 승인 시 문서가 종결됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "승인 불가(내 차례가 아님 / 이미 처리함) [P002, P005]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 [D001]"
            )
    })
    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApiResponse> approveDocument(
            @AuthenticationPrincipal String memberId,
            @PathVariable Long docId) {
        approvalService.approveDocument(docId, Long.valueOf(memberId));
        return ResponseEntity.ok(ApiResponse.success("결재 승인"));
    }

    @Operation(summary = "결재 반려", description = "문서를 반려합니다. 문서는 즉시 '반려(REJECTED)' 상태로 종결됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "반려 불가(내 차례가 아님 / 이미 처리함) [P002, P005]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 [D001]"
            )
    })
    @PutMapping("/{docId}/reject")
    public ResponseEntity<ApiResponse> rejectDocument(
            @AuthenticationPrincipal String memberId,
            @PathVariable Long docId) {
        approvalService.rejectDocument(docId, Long.valueOf(memberId));
        return ResponseEntity.ok(ApiResponse.success("결재 반려 완료"));
    }
}
