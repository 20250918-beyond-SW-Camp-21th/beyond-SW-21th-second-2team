package com.whatthefork.communicationandalarm.post.infrastructure.post;

import com.whatthefork.communicationandalarm.post.domain.Post;
import com.whatthefork.communicationandalarm.post.domain.PostRepository;
import com.whatthefork.communicationandalarm.post.domain.PostViewLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostViewLogJpaRepository postViewLogJpaRepository;

    @Override
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

    @Override
    public Long countViewsByPostId(Long postId) {
        return postViewLogJpaRepository.countByPostId(postId);
    }

    @Override
    public List<Post> findAllActive(Pageable pageable) {
        return postJpaRepository.findByIsDeletedFalse(pageable);
    }
}
