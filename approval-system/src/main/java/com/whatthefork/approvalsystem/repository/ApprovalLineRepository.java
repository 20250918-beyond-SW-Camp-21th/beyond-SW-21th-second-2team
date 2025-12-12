package com.whatthefork.approvalsystem.repository;

import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.enums.LineStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {
    @Transactional
    @Query("DELETE FROM ApprovalLine al WHERE al.document = :docId")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByDocumentId(@Param("docId") Long docId);

    List<ApprovalLine> findByDocumentOrderBySequence(Long document);

    @Transactional
    @Query("UPDATE ApprovalLine al " +
            "SET al.lineStatus = :status " +
            "WHERE al.document = :docId " +
            "AND al.sequence = :seq")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void updateLineStatusByDocumentAndSequence(
            @Param("docId") Long docId,
            @Param("seq") int seq,
            @Param("status") LineStatusEnum status
    );

    Optional<ApprovalLine> findByDocumentAndSequence(Long docId, int sequence);

    boolean existsByDocumentAndApprover(Long document, Long approver);

    Optional<ApprovalLine> findByDocumentAndSequenceAndApprover(Long document, int sequence, Long approver);

    boolean existsByDocumentAndSequence(Long document, int nextSequence);
}
