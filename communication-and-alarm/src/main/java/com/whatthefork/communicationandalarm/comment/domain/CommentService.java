package com.whatthefork.communicationandalarm.comment.domain;

import com.whatthefork.communicationandalarm.client.MemberClient;
import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.dto.response.UserDTO;
import com.whatthefork.communicationandalarm.common.dto.response.UserDetailResponse;
import com.whatthefork.communicationandalarm.common.utils.OffsetLimit;
import com.whatthefork.communicationandalarm.common.utils.Page;
import com.whatthefork.communicationandalarm.common.utils.PageUtil;
import com.whatthefork.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.response.CommentResponse;
import com.whatthefork.communicationandalarm.common.exception.ErrorCode;
import com.whatthefork.communicationandalarm.common.exception.GlobalException;
import com.whatthefork.communicationandalarm.post.domain.PostRepository;
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
    private final PostRepository postRepository;
    private final MemberClient  memberClient;

    private static final Long ROOT_DEPTH_SIZE = 0L;
    private static final Long MAX_DEPTH_SIZE = 2L;

    /*
    * 댓글 등록
    * */
    @Transactional
    public void create(String memberId, Long postId, CreateCommentRequest request) {

        ApiResponse<UserDetailResponse> info = memberClient.getUserDetail(memberId);
        UserDTO user = info.getData().getUser();

        Long memberIdLong = Long.parseLong(memberId);
        String memberName = user.getName();

        Long parentCommentId = request.getParentCommentId();
        Long depth = ROOT_DEPTH_SIZE;

        postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

      if (parentCommentId != null) {
            Comment parent = commentRepository
                    .findByCommentIdAndIsDeletedFalse(parentCommentId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parent.getPostId().equals(postId)) {
                throw new GlobalException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (parent.getDepth()  >= MAX_DEPTH_SIZE) {
                throw new GlobalException(ErrorCode.COMMENT_DEPTH_EXCEEDED);
            }
          depth = parent.getDepth() + 1L;
        }

        commentRepository.save(Comment.builder()
                .memberId(memberIdLong)
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
    public void update(String memberId, Long commentId, UpdateCommentRequest request) {

        Long memberIdLong = Long.parseLong(memberId);

        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.isOwner(memberIdLong)) {
            throw new GlobalException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.updateContent(request.getContent());
    }

    /*
    * 댓글 삭제
    * */
    @Transactional
    public void delete(String memberId, Long commentId) {

        Long memberIdLong = Long.parseLong(memberId);

        Comment comment = commentRepository
                .findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.isOwner(memberIdLong)) {
            throw new GlobalException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        boolean hasReply = commentRepository.existsByParentCommentIdAndIsDeletedFalse(commentId);
        if (hasReply) {
            throw new GlobalException(ErrorCode.COMMENT_CANNOT_DELETE);
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

