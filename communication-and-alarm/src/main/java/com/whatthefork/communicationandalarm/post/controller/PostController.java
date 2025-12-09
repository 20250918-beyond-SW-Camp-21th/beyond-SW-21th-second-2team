package com.whatthefork.communicationandalarm.post.controller;

import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.dto.request.CreatePostRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdatePostRequest;
import com.whatthefork.communicationandalarm.common.dto.response.GetPostResponse;
import com.whatthefork.communicationandalarm.common.dto.response.PostResponse;
import com.whatthefork.communicationandalarm.common.utils.Page;
import com.whatthefork.communicationandalarm.post.domain.PostService;
//import com.whatthefork.communicationandalarm.security.MemberClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequiredArgsConstructor
@Tag(name = "Post", description = "게시글")
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
//    private final MemberClient memberClient;

    /*
    * 게시글 등록
    * */
    @Operation(summary = "게시글 등록", description = "새 게시글을 등록합니다. ")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(
            /*@PathVariable Long memberId,*/
            @RequestParam Long memberId,
            @RequestParam String memberName,
            @RequestBody @Valid CreatePostRequest request
    ) {
        postService.create(memberId, memberName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    /*
     * 게시글 수정
     * */
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다. ")
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
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. ")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @RequestParam Long memberId,
            @PathVariable Long postId
    ) {
        postService.delete(memberId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    /*
    * 게시글 단건 조회
    * */
    @Operation(summary = "게시글 조회", description = "게시글을 조회합니다. ")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<GetPostResponse>> getPost(
            @PathVariable Long postId
    ) {
        GetPostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /*
    * 게시글 리스트 조회
    * */
    @Operation(summary = "게시글 리스트 조회", description = "게시글을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        Page<PostResponse> page = postService.getPosts(offset, limit);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
