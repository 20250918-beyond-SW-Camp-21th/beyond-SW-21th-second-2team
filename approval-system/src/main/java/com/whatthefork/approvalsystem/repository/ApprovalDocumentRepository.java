package com.whatthefork.approvalsystem.repository;

import com.whatthefork.approvalsystem.domain.ApprovalDocument;
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

    Page<ApprovalDocument> findByDocStatusAndDrafter(DocStatusEnum docStatus, Long drafter, Pageable pageable);

    Page<ApprovalDocument> findByDrafterAndDocStatusIn(Long drafter, List<DocStatusEnum> statusEnumList, Pageable pageable);

    Optional<ApprovalDocument> findByIdAndCurrentSequence(Long docId, int currentSequence);

    // 우선 전체 문서중, 멤버ID가 포함되고 그 포함된 결재선의 시퀀스와 문서의 시퀀스가 같은 것들의 문서를 불러와야함
    @Query(value = "SELECT d FROM ApprovalDocument d, ApprovalLine l " +
            "WHERE d.id = l.document " +
            "AND l.approver = :memberId " +
            "AND d.currentSequence = l.sequence " +
            "AND d.docStatus = 'IN_PROGRESS' " +
            "AND l.lineStatus = 'WAIT'")
    Page<ApprovalDocument> findDocumentsToApprove(@Param("memberId") Long memberId, Pageable pageable);

    /*
     * 1. History의 기안 id = Document의 기안 id
     * 2. History의 actor = memberId
     * 3. History에 타입이 APPROVE 혹은 REJECT 인것
     * 4. Document에 TEMP만 아니면 됨
     * */
    @Query(value = "SELECT DISTINCT d FROM ApprovalDocument d, ApprovalHistory h " +
            "WHERE d.id = h.document " +
            "AND h.actor = :memberId " +
            "AND h.actionType IN ('APPROVE', 'REJECT')")
    Page<ApprovalDocument> findProcessedDocuments(@Param("memberId") Long memberId, Pageable pageable);

    /*
     * 1. Referrer의 기안 id = Document의 기안 id
     * 2. Refferer의 referrer = memberId
     * 3.
     * */
    @Query(value = "SELECT d FROM ApprovalDocument d, ApprovalReferrer r " +
            "WHERE d.id = r.document " +
            "AND r.referrer = :memberId " +
            "AND d.docStatus != 'TEMP'")
    Page<ApprovalDocument> findReferencedDocuments(@Param("memberId") Long memberId, Pageable pageable);
}
