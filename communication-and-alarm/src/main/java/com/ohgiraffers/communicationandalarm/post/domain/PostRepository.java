package com.ohgiraffers.communicationandalarm.post.domain;

import java.util.Optional;

public interface PostRepository {
    Post save(Post post);

    PostViewLog save(PostViewLog postViewLog);

    Long countViewsByPostId(Long postId);

    Optional<Post> findByIdAndIsDeletedFalse(Long postId);
}
