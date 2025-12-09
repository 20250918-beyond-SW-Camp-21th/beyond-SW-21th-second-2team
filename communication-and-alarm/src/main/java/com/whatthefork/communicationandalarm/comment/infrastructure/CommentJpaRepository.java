package com.whatthefork.communicationandalarm.comment.infrastructure;

import com.whatthefork.communicationandalarm.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<Comment,Long> {


    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long commentId);

    boolean existsByParentCommentIdAndIsDeletedFalse(Long parentCommentId);

    Page<Comment> findByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    Long countByPostIdAndIsDeletedFalse(Long postId);
}
