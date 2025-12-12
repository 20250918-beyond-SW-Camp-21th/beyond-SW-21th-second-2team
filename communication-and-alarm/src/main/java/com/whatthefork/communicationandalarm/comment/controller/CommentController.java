package com.whatthefork.communicationandalarm.comment.controller;

import com.whatthefork.communicationandalarm.comment.domain.CommentService;
import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.response.CommentResponse;
import com.whatthefork.communicationandalarm.common.utils.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "Comment", description = "댓글")
public class CommentController {

    private final CommentService commentService;

    /*
    * 댓글 등록
    * */
    @Operation(summary = "댓글 등록", description = "새 댓글을 등록합니다. ")
    @PostMapping("{postId}")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @AuthenticationPrincipal String userIds,
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request
    ) {
        commentService.create(userIds, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    /*
    * 댓글 수정
    */
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다. ")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @AuthenticationPrincipal String userIds,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        commentService.update(userIds, commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }


    /*
    * 댓글 삭제
    * */
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. ")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal String userIds,
            @PathVariable Long commentId
    ) {
        commentService.delete(userIds, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));

    }

    /*
    *  댓글 리스트 조회
    * */
    @Operation(summary = "댓글 조회", description = "댓글을 조회합니다. ")
    @GetMapping("/{postId}/comments")
    public ApiResponse<Page<CommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.success(commentService.getComments(postId, offset, limit));
    }
}
