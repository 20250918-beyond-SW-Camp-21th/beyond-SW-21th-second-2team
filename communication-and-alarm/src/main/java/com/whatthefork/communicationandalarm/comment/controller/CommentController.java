package com.whatthefork.communicationandalarm.comment.controller;

import com.whatthefork.communicationandalarm.comment.domain.CommentService;
import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.response.CommentResponse;
import com.whatthefork.communicationandalarm.common.utils.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// @RequestMapping("/api/v1/post/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /*
    * 댓글 등록
    * */
    @PostMapping("{postId}")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @RequestParam Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request
    ) {
        commentService.create(memberId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    /*
    * 댓글 수정
    */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @RequestParam Long memberId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        // memberId = 1L;
        commentService.update(memberId, commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }


    /*
    * 댓글 삭제
    * */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @RequestParam Long memberId,
            @PathVariable Long commentId
    ) {
        commentService.delete(memberId, /*memberId*/commentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));

    }

    /*
    *  댓글 리스트 조회 (페이지네이션)
    * */
    @GetMapping("/{postId}/comments")
    public ApiResponse<Page<CommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.success(commentService.getComments(postId, offset, limit));
    }
}
