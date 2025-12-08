package com.ohgiraffers.communicationandalarm.post.infrastructure.post;

import com.ohgiraffers.communicationandalarm.post.domain.Post;
import com.ohgiraffers.communicationandalarm.post.domain.PostRepository;
import com.ohgiraffers.communicationandalarm.post.domain.PostViewLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostViewLogJpaRepository postViewLogJpaRepository;

    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public PostViewLog save(PostViewLog postViewLog) {
        return postViewLogJpaRepository.save(postViewLog);
    }

    @Override
    public Optional<Post> findByIdAndIsDeletedFalse(Long postId) {
        return postJpaRepository.findByIdAndIsDeletedFalse(postId);
    }

    public Long countViewsByPostId(Long postId) {
        return postViewLogJpaRepository.countByPostId(postId);
    }
}
