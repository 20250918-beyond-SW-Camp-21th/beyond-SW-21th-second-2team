package com.whatthefork.communicationandalarm.post.domain;

import com.whatthefork.communicationandalarm.comment.domain.CommentRepository;
import com.whatthefork.communicationandalarm.common.enums.Category;
import com.whatthefork.communicationandalarm.common.utils.OffsetLimit;
import com.whatthefork.communicationandalarm.common.utils.Page;
import com.whatthefork.communicationandalarm.common.utils.PageUtil;
import com.whatthefork.communicationandalarm.common.dto.request.CreatePostRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdatePostRequest;
import com.whatthefork.communicationandalarm.common.dto.response.GetPostResponse;
import com.whatthefork.communicationandalarm.common.dto.response.PostResponse;
import com.whatthefork.communicationandalarm.common.exception.ErrorCode;
import com.whatthefork.communicationandalarm.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /*
     * 게시글 등록
     * */
    @Transactional
    public void create(Long memberId, String memberName, CreatePostRequest request) {

        Category category = request.getCategory();
        // 임시 관리자 검증
        boolean isAdmin = memberId != null && memberId.equals(1L);

        if (category == Category.ANNOUNCEMENT && !isAdmin) {
            throw new GlobalException(ErrorCode.POST_ACCESS_DENIED);
        }


        Post post = Post.create(
                memberId,
                memberName,
                request.getTitle(),
                request.getContent(),
                category
        );

        if (category == Category.ANNOUNCEMENT) {
            post.pin();
        }

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

        Category category = request.getCategory();

        // 임시 관리자 검증
        boolean isAdmin = memberId != null && memberId.equals(1L);

        if (category == Category.ANNOUNCEMENT && !isAdmin) {
            throw new GlobalException(ErrorCode.POST_ACCESS_DENIED);
        }

        post.update(
                request.getTitle(),
                request.getContent(),
                category
        );
        if (category == Category.ANNOUNCEMENT) {
            post.pin();
        } else {
            post.unpin();
        }
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
    @Transactional(readOnly = true)
    public GetPostResponse getPost(Long postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

        // 임시 멤버
        Long memberId = 1L;
        PostViewLog viewLog = PostViewLog.create(memberId, postId);
        postRepository.save(viewLog);

        Long viewCount = postRepository.countViewsByPostId(postId);
        Long commentCount = commentRepository.countByPostId(postId);

        return GetPostResponse.of(post, viewCount, commentCount);
    }

    /*
     * 게시글 리스트 조회
     * */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(int offset, int limit) {

        OffsetLimit offsetLimit = new OffsetLimit(offset, limit);
        Pageable pageable = offsetLimit.toPageable();

        List<Post> posts = postRepository.findAllActive(pageable);

        List<PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            Long viewCount = postRepository.countViewsByPostId(post.getId());
            Long commentCount = commentRepository.countByPostId(post.getId());
            responses.add(PostResponse.from(post, viewCount, commentCount));
        }

        boolean hasNext = PageUtil.hasNext(posts, limit);

        return Page.<PostResponse>builder()
                .contents(responses)
                .hasNext(hasNext)
                .build();
    }
}
