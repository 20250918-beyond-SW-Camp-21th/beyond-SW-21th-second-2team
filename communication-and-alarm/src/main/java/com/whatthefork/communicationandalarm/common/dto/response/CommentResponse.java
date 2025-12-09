package com.whatthefork.communicationandalarm.common.dto.response;

import com.whatthefork.communicationandalarm.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long commentId;
    private Long memberId;
    private String memberName;
    private String content;
    private Long depth;
    private Long parentCommentId;
    private Long postId;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .memberId(comment.getMemberId())
                .memberName(comment.getMemberName())
                .content(comment.getContent())
                .depth(comment.getDepth())
                .parentCommentId(comment.getParentCommentId())
                .postId(comment.getPostId())
                .build();
    }
}
