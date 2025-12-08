package com.approvalsystem.repository;

import com.approvalsystem.domain.ApprovalDocument;
import com.approvalsystem.enums.DocStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Long> {
    Optional<ApprovalDocument> findById(long id);

    Page<ApprovalDocument> findByDocStatusAndDrafterOrderByCreatedAtDesc(DocStatusEnum docStatus, Long drafter, Pageable pageable);

    Page<ApprovalDocument> findByDocStatusAndDocStatusIn(List<DocStatusEnum> statusEnumList, Long drafter, Pageable pageable);
}
