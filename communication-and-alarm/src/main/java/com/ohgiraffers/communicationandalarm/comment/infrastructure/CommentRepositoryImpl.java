package com.ohgiraffers.communicationandalarm.comment.infrastructure;

import com.ohgiraffers.communicationandalarm.comment.domain.Comment;
import com.ohgiraffers.communicationandalarm.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findByCommentIdAndIsDeletedFalse(Long commentId) {
        return commentJpaRepository.findByCommentIdAndIsDeletedFalse(commentId);
    }

    @Override
    public boolean existsByParentCommentIdAndIsDeletedFalse(Long parentCommentId) {
        return commentJpaRepository.existsByParentCommentIdAndIsDeletedFalse(parentCommentId);
    }
}
