package com.whatthefork.approvalsystem.controller;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.dto.request.CreateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.request.UpdateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.response.DocumentDetailResponseDto;
import com.whatthefork.approvalsystem.dto.response.DocumentListResponseDto;
import com.whatthefork.approvalsystem.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /* 12.06 모든 메소드의 매개변수에 userId 대신 @AuthenticationPrincipal UserDetailsImpl로 로그인된 사용자 정보에서 Id 추출할 예정. 현재는 임시 기입 */
    @PostMapping("/drafting")
    public ResponseEntity<ApiResponse> createDocument(/*Long userId, */@RequestBody @Valid CreateDocumentRequestDto requestDto) {
        /* 12.07 security 완성 전까지 임시 유저 ID는 1로 고정해서 API 테스트 */
        Long memberId = 101L;
        Long docId = documentService.createDocument(memberId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(docId));
    }

    @PutMapping("/{docId}")
    public ResponseEntity<ApiResponse> updateDocument(/*Long memberId*/ @PathVariable Long docId, @RequestBody @Valid UpdateDocumentRequestDto requestDto) {
        Long memberId = 101L;
        documentService.updateDocument(memberId, docId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("기안 수정 완료"));
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<ApiResponse> deleteDocument(/*Long memberId,*/ @PathVariable Long docId) {
        Long memberId = 101L;
        documentService.deleteDocument(memberId, docId);
        return ResponseEntity.ok(ApiResponse.success("기안 삭제 완료"));
    }

    @GetMapping("/{docId}")
    public ResponseEntity<ApiResponse> getDocumentDetail(/*Long memberId,*/ @PathVariable Long docId) {
        Long memberId = 101L;
        documentService.writeReadHistory(memberId, docId);
        DocumentDetailResponseDto response = documentService.readDetailDocument(memberId, docId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/drafts")
    public ResponseEntity<ApiResponse> getTempDocumentList(/*Long memberId,*/ Pageable pageable) {
        Long memberId = 101L;
        Page<DocumentListResponseDto> documentList = documentService.getTempDocumentList(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse> getProgressDocumentList(/*Long memberId,*/ Pageable pageable) {
        Long memberId = 101L;
        Page<DocumentListResponseDto> documentList = documentService.getProgressDocumentList(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @GetMapping("/closed")
    public ResponseEntity<ApiResponse> getClosedDocumentList(/*Long memberId,*/ Pageable pageable) {
        Long memberId = 101L;
        Page<DocumentListResponseDto> documentList = documentService.getClosedDocumentList(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getDocumentsToApprove(/*Long memberId*/ Pageable pageable) {
        Long memberId = 101L;
        Page<DocumentListResponseDto> documentList = documentService.getDocumentsToApprove(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @GetMapping("/processed")
    public ResponseEntity<ApiResponse> getProcessedDocuments(/*Long memberId*/ Pageable pageable) {
        Long memberId = 101L;
        Page<DocumentListResponseDto> documentList = documentService.getProcessedDocuments(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }

    @GetMapping("/referenced")
    public ResponseEntity<ApiResponse> getReferencedDocuments(/*Long memberId*/ Pageable pageable) {
        Long memberId = 101L;
        Page<DocumentListResponseDto> documentList = documentService.getReferencedDocuments(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(documentList));
    }
}
