package com.whatthefork.communicationandalarm.comment.domain;

import com.whatthefork.communicationandalarm.common.utils.OffsetLimit;
import com.whatthefork.communicationandalarm.common.utils.Page;
import com.whatthefork.communicationandalarm.common.utils.PageUtil;
import com.whatthefork.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.response.CommentResponse;
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
public class CommentService {

    private final CommentRepository commentRepository;

    private static final Long ROOT_DEPTH_SIZE = 0L;
    private static final Long FIRST_DEPTH_SIZE = 1L;
    private static final Long MAX_DEPTH_SIZE = 2L;

    /*
    * 댓글 등록
    * */
    @Transactional
    public void create(Long memberId, Long postId, CreateCommentRequest request) {
        // 임시 댓글 부모 아이디. (root 댓글)
        Long parentCommentId = request.getParentCommentId();
        // 임시 depth
        Long depth = ROOT_DEPTH_SIZE;

      if (parentCommentId != null) {
            Comment parent = commentRepository
                    .findByCommentIdAndIsDeletedFalse(parentCommentId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parent.getPostId().equals(postId)) {
                throw new GlobalException(ErrorCode.INVALID_INPUT_VALUE);
            }

            Long parentDepth = parent.getDepth();
            if (parentDepth >= MAX_DEPTH_SIZE) {
                throw new GlobalException(ErrorCode.COMMENT_DEPTH_EXCEEDED);
            }
          depth = parent.getDepth() + 1L;
        }

        commentRepository.save(Comment.builder()
                .memberId(memberId)
                .postId(postId)
                .memberName("테스트")
                .parentCommentId(parentCommentId)
                .depth(depth)
                .content(request.getContent())
                .isDeleted(false)
                .build());
    }

    /*
    * 댓글 수정
    * */
    @Transactional
    public void update(Long memberId, Long commentId, UpdateCommentRequest request) {

        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.isOwner(memberId)) {
            throw new GlobalException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.updateContent(request.getContent());
    }

    /*
    * 댓글 삭제
    * */
    @Transactional
    public void delete(Long memberId, Long commentId) {

        Comment comment = commentRepository
                .findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        // 작성자 검증
        if (!comment.isOwner(memberId)) {
            throw new GlobalException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        // 대댓글이 있는지 검사
        boolean hasReply = commentRepository.existsByParentCommentIdAndIsDeletedFalse(commentId);
        if (hasReply) {
            throw new GlobalException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.delete();
    }
    
    /*
    * 댓글 조회
    * */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long postId, int offset, int limit) {

        OffsetLimit offsetLimit = new OffsetLimit(offset, limit);
        Pageable pageable = offsetLimit.toPageable();

        List<Comment> comments = commentRepository.findPageByPostId(postId, pageable);

        List<CommentResponse> responses = new ArrayList<>();
        for (Comment comment : comments) {
            responses.add(CommentResponse.from(comment));
        }

        boolean hasNext = PageUtil.hasNext(comments, limit);

        return Page.<CommentResponse>builder()
                .contents(responses)
                .hasNext(hasNext)
                .build();
    }
}

