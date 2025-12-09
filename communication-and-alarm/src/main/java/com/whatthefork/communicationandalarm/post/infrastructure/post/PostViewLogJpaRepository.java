package com.whatthefork.communicationandalarm.post.infrastructure.post;

import com.whatthefork.communicationandalarm.post.domain.PostViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewLogJpaRepository extends JpaRepository<PostViewLog, Long> {

    Long countByPostId(Long postId);


}
