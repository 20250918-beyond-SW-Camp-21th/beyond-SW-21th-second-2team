package com.whatthefork.communicationandalarm.post.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    PostViewLog save(PostViewLog postViewLog);

    Long countViewsByPostId(Long postId);

    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

    List<Post> findAllActive(Pageable pageable);
}
