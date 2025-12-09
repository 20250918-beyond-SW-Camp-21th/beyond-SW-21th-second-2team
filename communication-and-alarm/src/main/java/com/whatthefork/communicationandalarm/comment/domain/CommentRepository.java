package com.whatthefork.communicationandalarm.comment.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository{

    Comment save(Comment comment);

    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long id);

    boolean existsByParentCommentIdAndIsDeletedFalse(Long parentCommentId);

    List<Comment> findPageByPostId(Long postId, Pageable pageable);

    Long countByPostId(Long postId);
}
