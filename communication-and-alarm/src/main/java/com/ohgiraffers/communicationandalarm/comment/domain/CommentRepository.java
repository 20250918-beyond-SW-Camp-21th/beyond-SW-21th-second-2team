package com.ohgiraffers.communicationandalarm.comment.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository{

    Comment save(Comment comment);

    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long id);

    boolean existsByParentCommentIdAndIsDeletedFalse(Long parentCommentId);
}
