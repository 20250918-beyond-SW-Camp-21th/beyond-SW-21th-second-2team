package com.whatthefork.approvalsystem.repository;

import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Long> {
    Optional<ApprovalDocument> findById(long id);

    ApprovalDocument findApprovalDocumentById(long id);

    Page<ApprovalDocument> findByDocStatusAndDrafterOrderByCreatedAtDesc(DocStatusEnum docStatus, Long drafter, Pageable pageable);

    Page<ApprovalDocument> findByDrafterAndDocStatusInOrderByCreatedAtDesc(Long drafter, List<DocStatusEnum> statusEnumList, Pageable pageable);

    Optional<ApprovalDocument> findByIdAndCurrentSequence(Long docId, int currentSequence);

    // 우선 전체 문서중, 멤버ID가 포함되고 그 포함된 결재선의 시퀀스와 문서의 시퀀스가 같은 것들의 문서를 불러와야함
    @Query("SELECT d FROM ApprovalDocument d " +
            "JOIN ApprovalLine l ON d.id = l.document " +
            "WHERE l.approver = :memberId " +
            "AND d.currentSequence = l.sequence " +
            "AND d.docStatus = 'IN_PROGRESS'")
    Page<ApprovalDocument> findDocumentsToApprove(Long memberId, Pageable pageable);

    /*
     * 1. History의 기안 id = Document의 기안 id
     * 2. History의 actor = memberId
     * 3. History에 타입이 APPROVE 혹은 REJECT 인것
     * 4. Document에 TEMP만 아니면 됨
     * */
    @Query(" SELECT DISTINCT d FROM ApprovalDocument d " +
            "JOIN ApprovalHistory h on d.id = h.document " +
            "WHERE h.actor = :memberId " +
            "AND h.actionType IN ('APPROVE', 'REJECT')")
    Page<ApprovalDocument> findProcessedDocuments(Long memberId, Pageable pageable);

    /*
     * 1. Referrer의 기안 id = Document의 기안 id
     * 2. Refferer의 referrer = memberId
     * 3.
     * */
    @Query("SELECT d FROM ApprovalDocument d " +
            "JOIN ApprovalReferrer r on d.id = r.document " +
            "WHERE r.referrer = :memberId " +
            "AND d.docStatus != 'TEMP'")
    Page<ApprovalDocument> findReferencedDocuments(Long memberId, Pageable pageable);
}
