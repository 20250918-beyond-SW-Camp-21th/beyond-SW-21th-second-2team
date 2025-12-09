package com.whatthefork.communicationandalarm.comment.infrastructure;

import com.whatthefork.communicationandalarm.comment.domain.Comment;
import com.whatthefork.communicationandalarm.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<Comment> findPageByPostId(Long postId, Pageable pageable) {
        return commentJpaRepository
                .findByPostIdAndIsDeletedFalse(postId, pageable)
                .getContent();
    }

    @Override
    public Long countByPostId(Long postId) {
        return commentJpaRepository.countByPostIdAndIsDeletedFalse(postId);
    }
}
