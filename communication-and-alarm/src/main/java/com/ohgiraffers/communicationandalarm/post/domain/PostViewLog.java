package com.ohgiraffers.communicationandalarm.post.domain;

import com.ohgiraffers.communicationandalarm.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostViewLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long postId;

    private PostViewLog(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }

    public static PostViewLog create(Long memberId, Long postId) {
        return new PostViewLog(memberId, postId);
    }
}
