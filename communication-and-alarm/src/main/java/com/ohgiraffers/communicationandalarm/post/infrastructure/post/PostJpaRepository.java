package com.ohgiraffers.communicationandalarm.post.infrastructure.post;

import com.ohgiraffers.communicationandalarm.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post,Long> {

    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

}
