package com.ohgiraffers.communicationandalarm.comment.domain;

import com.ohgiraffers.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.ohgiraffers.communicationandalarm.common.dto.request.UpdateCommentRequest;
import com.ohgiraffers.communicationandalarm.common.exception.ErrorCode;
import com.ohgiraffers.communicationandalarm.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void create(Long memberId, String memberName, Long postId, CreateCommentRequest request) {
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

        Comment comment = commentRepository.save(Comment.builder()
                .memberId(memberId)
                .postId(postId)
                .memberName(memberName)
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

        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(request.getCommentId())
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
}

