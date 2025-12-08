package com.ohgiraffers.communicationandalarm.post.controller;

import com.ohgiraffers.communicationandalarm.common.ApiResponse;
import com.ohgiraffers.communicationandalarm.common.dto.request.CreatePostRequest;
import com.ohgiraffers.communicationandalarm.common.dto.request.UpdatePostRequest;
import com.ohgiraffers.communicationandalarm.post.domain.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /*
    * 게시물 등록
    * */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(
            @RequestParam Long memberId,
            @RequestBody @Valid CreatePostRequest request
    ) {
        postService.create(memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    /*
     * 게시물 수정
     * */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestParam Long memberId,
            @RequestBody @Valid UpdatePostRequest request
    ) {
        postService.update(memberId, postId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    /*
    *  게시물 삭제
    * */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @RequestParam Long memberId,
            @PathVariable Long postId
    ) {
        postService.delete(memberId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    /*
    * 게시물 조회
    * */
//    @GetMapping("/{postId}")
//    public ResponseEntity<ApiResponse<PostResponse>> getPost(
//            @RequestParam Long memberId,
//            @PathVariable("postId") Long postId
//    ) {
//        GetPostResponse response = postService.getPost(postId, memberId);
//        return ApiResponse.success(response);
//    }
}
