package com.ohgiraffers.communicationandalarm.post.domain;

import com.ohgiraffers.communicationandalarm.common.dto.request.CreatePostRequest;
import com.ohgiraffers.communicationandalarm.common.dto.request.UpdatePostRequest;
import com.ohgiraffers.communicationandalarm.common.dto.response.GetPostResponse;
import com.ohgiraffers.communicationandalarm.common.exception.ErrorCode;
import com.ohgiraffers.communicationandalarm.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /*
    * 게시글 등록
    * */
    @Transactional
    public void create(Long memberId, CreatePostRequest request) {
        // username 받아와서 사용할 예정입니다.
        Post post = Post.create(
                memberId,
                "테스트유저이름",
                request.getTitle(),
                request.getContent(),
                request.getCategory()
        );
        postRepository.save(post);
    }

    /*
     * 게시글 수정
     * */
    @Transactional
    public void update(Long memberId, Long postId, UpdatePostRequest request) {

        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

        if (!post.isOwner(memberId)) {
            throw new GlobalException(ErrorCode.POST_ACCESS_DENIED);
        }

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getCategory()
        );
    }

    /*
    * 게시글 삭제
    * */
    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

        if (!post.isOwner(memberId)) {
            throw new GlobalException(ErrorCode.POST_ACCESS_DENIED);
        }
        post.delete();
    }

    /*
    * 게시글 조회
    * */
    @Transactional
    public GetPostResponse get(Long memberId, Long postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

        PostViewLog viewLog = PostViewLog.create(memberId, postId);
        postRepository.save(viewLog);

        Long viewCount = postRepository.countViewsByPostId(postId);
        return GetPostResponse.of(post, viewCount);
    }
}
