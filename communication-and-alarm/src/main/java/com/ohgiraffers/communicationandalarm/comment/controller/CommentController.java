package com.ohgiraffers.communicationandalarm.comment.controller;

import com.ohgiraffers.communicationandalarm.comment.domain.CommentService;
import com.ohgiraffers.communicationandalarm.common.ApiResponse;
import com.ohgiraffers.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.ohgiraffers.communicationandalarm.common.dto.request.UpdateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /*
    * 댓글 등록
    * */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request
    ) {
        // 임시 멤버id 와 이름
        Long memberId = 1L;
        String memberName = "정상수";
        commentService.create(memberId, memberName, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    /*
    * 댓글 수정
    */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        // 임시 멤버id 와 이름
        Long memberId = 1L;

        commentService.update(postId, commentId,  request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    /*
    * 댓글 삭제
    * */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {

        // 임시 멤버id 와 이름
        Long memberId = 1L;

        commentService.delete(memberId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));

    }

    /*
    *  댓글 조회
    * */
    public ResponseEntity<ApiResponse<Void>> deleteAllComments() {
        return null;
    }
}
