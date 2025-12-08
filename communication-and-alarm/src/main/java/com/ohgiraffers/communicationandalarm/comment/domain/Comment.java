package com.ohgiraffers.communicationandalarm.comment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment {

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "member_id",  nullable = false)
    private Long memberId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_id")
    private Long parentCommentId;

    @Column(name = "memberName", nullable = false)
    private String memberName;

    @Column(name = "depth", nullable = false)
    private Long depth = 0L;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    public Boolean isDeleted = false;

    @Builder
    public Comment(Long memberId, Long postId, Long parentCommentId, String memberName, Long depth, String content, Boolean isDeleted) {
        this.memberId = memberId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.memberName = memberName;
        this.depth = depth;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public boolean isOwner(Long memberId) {
        return this.memberId.equals(memberId);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
