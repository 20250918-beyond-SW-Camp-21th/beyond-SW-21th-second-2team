package com.approvalsystem.repository;

import com.approvalsystem.domain.ApprovalReferrer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalReferrerRepository extends JpaRepository<ApprovalReferrer, Long> {
    List<ApprovalReferrer> findByDocumentOrderByViewedAt(Long documentId);
}
