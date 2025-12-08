package com.ohgiraffers.communicationandalarm.comment.infrastructure;

import com.ohgiraffers.communicationandalarm.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<Comment,Long> {


    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long commentId);

    List<Comment> findByPostIdAndIsDeletedFalseOrderByCommentIdAsc(Long postId);

    boolean existsByParentCommentIdAndIsDeletedFalse(Long parentCommentId);

}
