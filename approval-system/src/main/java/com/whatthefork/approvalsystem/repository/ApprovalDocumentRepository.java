package com.whatthefork.approvalsystem.repository;

import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Long> {
    Optional<ApprovalDocument> findById(long id);

    Page<ApprovalDocument> findByDocStatusAndDrafterOrderByCreatedAtDesc(DocStatusEnum docStatus, Long drafter, Pageable pageable);

    Page<ApprovalDocument> findByDrafterAndDocStatusInOrderByCreatedAtDesc(Long drafter, List<DocStatusEnum> statusEnumList, Pageable pageable);
}
