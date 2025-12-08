package com.ohgiraffers.communicationandalarm.post.infrastructure.post;

import com.ohgiraffers.communicationandalarm.post.domain.PostViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewLogJpaRepository extends JpaRepository<PostViewLog, Long> {

    Long countByPostId(Long postId);
}
