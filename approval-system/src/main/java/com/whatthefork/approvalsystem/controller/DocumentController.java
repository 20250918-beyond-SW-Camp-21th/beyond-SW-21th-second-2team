package com.whatthefork.approvalsystem.controller;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.dto.request.CreateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.request.UpdateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.response.DocumentDetailResponseDto;
import com.whatthefork.approvalsystem.dto.response.DocumentListResponseDto;
import com.whatthefork.approvalsystem.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Document", description = "결재 문서 관리 API (기안, 조회, 수정, 삭제, 각종 문서함)")
@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "결재 문서 기안 (생성)", description = "새로운 결재 문서를 작성합니다. 성공 시 문서 ID를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류 / 결재자 3명 아님 / 본인을 결재자로 지정 [C001, D004, D005, D006]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "지정한 결재자(멤버)를 찾을 수 없음 [C002]"
            )
    })
    @PostMapping("/drafting")
    public ResponseEntity<ApiResponse> createDocument(
            @AuthenticationPrincipal String memberId,
            @RequestBody @Valid CreateDocumentRequestDto requestDto) {
        Long docId = documentService.createDocument(memberId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(docId));
    }

    @Operation(summary = "기안 문서 수정", description = "임시저장 상태인 문서를 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "수정 불가(이미 결재 진행됨) / 입력값 오류 [D002, C001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음(본인 문서 아님) [A001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 [D001]"
            )
    })
    @PutMapping("/{docId}")
    public ResponseEntity<ApiResponse> updateDocument(
            @AuthenticationPrincipal String memberId,
            @PathVariable Long docId,
            @RequestBody @Valid UpdateDocumentRequestDto requestDto) {
        documentService.updateDocument(Long.valueOf(memberId), docId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("기안 수정 완료"));
    }

    @Operation(summary = "기안 문서 삭제", description = "문서를 삭제(Soft Delete)합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "삭제 불가(이미 결재 진행됨) [D003]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음(본인 문서 아님) [A001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 [D001]"
            )
    })
    @DeleteMapping("/{docId}")
    public ResponseEntity<ApiResponse> deleteDocument(
            @AuthenticationPrincipal String memberId,
            @PathVariable Long docId) {
        documentService.deleteDocument(Long.valueOf(memberId), docId);
        return ResponseEntity.ok(ApiResponse.success("기안 삭제 완료"));
    }

    @Operation(summary = "문서 상세 조회", description = "문서의 상세 내용 및 결재선/참조자 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "열람 권한 없음(결재자/참조자 아님) [A002]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음(작성자만 조회 가능 로직) [A001]"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "문서 없음 [D001]"
            )
    })
    @GetMapping("/{docId}")
    public ResponseEntity<ApiResponse> getDocumentDetail(
            @AuthenticationPrincipal String memberIdStr,
            @PathVariable Long docId) {
        documentService.writeReadHistory(docId, memberIdStr);
        DocumentDetailResponseDto response = documentService.readDetailDocument(memberIdStr, docId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "임시저장 문서함", description = "내가 작성 중인(임시저장) 문서를 조회합니다.")
    @GetMapping("/drafts")
    public ResponseEntity<ApiResponse> getTempDocumentList(
            @AuthenticationPrincipal String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<DocumentListResponseDto> documentList = documentService.getTempDocumentList(Long.valueOf(memberId), pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @Operation(summary = "결재 진행함 (상신)", description = "내가 기안하여 현재 결재가 진행 중인 문서를 조회합니다.")
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse> getProgressDocumentList(
            @AuthenticationPrincipal String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<DocumentListResponseDto> documentList = documentService.getProgressDocumentList(Long.valueOf(memberId), pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @Operation(summary = "결재 완료함 (종결)", description = "내가 기안하여 승인 완료되거나 반려된 문서를 조회합니다.")
    @GetMapping("/closed")
    public ResponseEntity<ApiResponse> getClosedDocumentList(
            @AuthenticationPrincipal String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<DocumentListResponseDto> documentList = documentService.getClosedDocumentList(Long.valueOf(memberId), pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @Operation(summary = "결재 대기함 (미결)", description = "내가 결재해야 할 차례인 문서를 조회합니다.")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getDocumentsToApprove(
            @AuthenticationPrincipal String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<DocumentListResponseDto> documentList =
                documentService.getDocumentsToApprove(Long.valueOf(memberId), pageable);

        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @Operation(summary = "기결재함 (처리완료)", description = "내가 승인하거나 반려 처리한 문서를 조회합니다.")
    @GetMapping("/processed")
    public ResponseEntity<ApiResponse> getProcessedDocuments(
            @AuthenticationPrincipal String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<DocumentListResponseDto> documentList = documentService.getProcessedDocuments(Long.valueOf(memberId), pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @Operation(summary = "참조 문서함", description = "내가 참조자로 지정된 문서를 조회합니다.")
    @GetMapping("/referenced")
    public ResponseEntity<ApiResponse> getReferencedDocuments(
            @AuthenticationPrincipal String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<DocumentListResponseDto> documentList = documentService.getReferencedDocuments(Long.valueOf(memberId), pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }
}
