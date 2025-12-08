package com.approvalsystem.service;

import com.approvalsystem.common.error.BusinessException;
import com.approvalsystem.common.error.ErrorCode;
import com.approvalsystem.domain.ApprovalDocument;
import com.approvalsystem.domain.ApprovalHistory;
import com.approvalsystem.domain.ApprovalLine;
import com.approvalsystem.domain.ApprovalReferrer;
import com.approvalsystem.domain.member.MemberRepository;
import com.approvalsystem.dto.request.CreateDocumentRequestDto;
import com.approvalsystem.dto.request.UpdateDocumentRequestDto;
import com.approvalsystem.dto.response.ApprovalLineResponseDto;
import com.approvalsystem.dto.response.DocumentDetailResponseDto;
import com.approvalsystem.dto.response.DocumentListResponseDto;
import com.approvalsystem.dto.response.ReferrerResponseDto;
import com.approvalsystem.enums.ActionTypeEnum;
import com.approvalsystem.enums.DocStatusEnum;
import com.approvalsystem.enums.LineStatusEnum;
import com.approvalsystem.repository.ApprovalDocumentRepository;
import com.approvalsystem.repository.ApprovalHistoryRepositoy;
import com.approvalsystem.repository.ApprovalLineRepository;
import com.approvalsystem.repository.ApprovalReferrerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/* 12.06 UserDetails 부분이 완성이 되면 모든 메소드의 Long userId는 Userdetials로 교체할 것 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ApprovalDocumentRepository approvalDocumentRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalHistoryRepositoy approvalHistoryRepositoy;
    private final ApprovalReferrerRepository approvalReferrerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createDocument(Long memberId, CreateDocumentRequestDto requestDto) {

        // 결재선 저장
        List<Long> approvalIds = requestDto.getApproverIds();
        validateApproverList(approvalIds);

        /* (추후 구현) memberRepository에서 해당 멤버가 존재하는지 getDraftId로 판독 */

        // 기안 작성
        ApprovalDocument approvalDocument = ApprovalDocument.builder()
                        .drafter(memberId)
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .createdAt(LocalDateTime.now())
                        .currentSequence(1)
                        .docStatus(DocStatusEnum.TEMP)
                        .version(1L)
                        .startVacationDate(requestDto.getStartVacationDate())
                        .endVacationDate(requestDto.getEndVacationDate())
                        .build();

        ApprovalDocument savedDoc = approvalDocumentRepository.save(approvalDocument);
        Long documentId = savedDoc.getId();

        // 결재선 등록
        createApprovalLines(documentId, approvalIds);

        // 참조자 설정
        if(requestDto.getReferrer() != null) {
            for(Long referrerId : requestDto.getReferrer()) {
                ApprovalReferrer approvalReferrer = ApprovalReferrer.builder()
                        .document(documentId)
                        .referrer(referrerId)
                        .viewedAt(null)
                        .build();

                approvalReferrerRepository.save(approvalReferrer);
            }
        }

        // 결재 로그 저장
        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(documentId)
                .actor(memberId)
                .actionType(ActionTypeEnum.CREATE)
                .build();

        approvalHistoryRepositoy.save(approvalHistory);

        return savedDoc.getId();
    }

    @Transactional
    public void updateDocument(Long memberId, Long docId, UpdateDocumentRequestDto requestDto) {
        List<Long> approvalIds = requestDto.getApproverIds();
        validateApproverList(approvalIds);

        ApprovalDocument approvalDocument = validateUpdateAuthority(memberId, docId);

        approvalDocument.updateDocument(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getStartVacationDate(),
                requestDto.getEndVacationDate()
        );

        approvalLineRepository.deleteByDocumentId(docId);
        createApprovalLines(docId, approvalIds);
    }

    @Transactional
    public void deleteDocument(Long userId, Long docId) {
        ApprovalDocument approvalDocument = validateDeleteAuthority(userId, docId);
        approvalDocument.deleteDocument();
    }


    @Transactional(readOnly = true)
    public DocumentDetailResponseDto readDetailDocument(Long memberId, Long docId) {
       ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

       if(!document.isSameDrafter(memberId)) {
           throw new BusinessException(ErrorCode.NOT_DRAFTER);
       }

       /* 문서 아이디로 결재선과 참조자 목록을 모두 가져옴 */
       List<ApprovalLine> approvalLines = approvalLineRepository.findByDocumentOrderBySequence(docId);
       List<ApprovalReferrer> referrers = approvalReferrerRepository.findByDocumentOrderByViewedAt(docId);

        List<ApprovalLineResponseDto> approvalLinesResponseDto = approvalLines.stream()
                .map(line -> ApprovalLineResponseDto.builder()
                        .approverId(line.getApprover())
                        .approverName("임시 이름") // 12.08 memberRepository 연동 후 수정 예정
                        .sequence(line.getSequence())
                        .status(line.getLineStatus())
                        .approvedAt(line.getApprovedAt())
                        .build()
                )
                .toList();

        List<ReferrerResponseDto> referrerResponseDto = referrers.stream().map(
                referrer -> ReferrerResponseDto.builder()
                        .referrerId(referrer.getReferrer())
                        .referrerName("임시 이름") // 12.08 memberRepository 연동 후 수정
                        .viewedAt(referrer.getViewedAt())
                        .build()
        ).toList();

       DocumentDetailResponseDto responseDto = DocumentDetailResponseDto.builder()
               .documentId(docId)
               .title(document.getTitle())
               .content(document.getContent())
               .docStatus(document.getDocStatus())
               .drafterId(document.getDrafter())
               .startVacationDate(document.getStartVacationDate())
               .endVacationDate(document.getEndVacationDate())
               .createdAt(document.getCreatedAt())
               .approvers(approvalLinesResponseDto)
               .referrers(referrerResponseDto)
               .build();

       return responseDto;
    }

    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getTempDocumentList(Long memberId, Pageable pageable) {
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findByDocStatusAndDrafterOrderByCreatedAtDesc(DocStatusEnum.TEMP, memberId, pageable);

        return documentList.map(document ->
                DocumentListResponseDto.builder()
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .status(document.getDocStatus())
                        .createdDate(document.getCreatedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getProgressDocumentList(Long memberId, Pageable pageable) {
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findByDocStatusAndDrafterOrderByCreatedAtDesc(DocStatusEnum.IN_PROGRESS, memberId, pageable);

        return documentList.map(document ->
                DocumentListResponseDto.builder()
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .status(document.getDocStatus())
                        .createdDate(document.getCreatedAt())
                        .build()
        );
    }


    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getClosedDocumentList(Long memberId, Pageable pageable) {
        List<DocStatusEnum> statuses = Arrays.asList(DocStatusEnum.APPROVED, DocStatusEnum.REJECTED);

        Page<ApprovalDocument> documentList = approvalDocumentRepository.findByDocStatusAndDocStatusIn(statuses, memberId, pageable);

        return documentList.map(document ->
                DocumentListResponseDto.builder()
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .status(document.getDocStatus())
                        .createdDate(document.getCreatedAt())
                        .build()
        );
    }

    private ApprovalDocument validateUpdateAuthority(Long userId, Long docId) {
        ApprovalDocument approvalDocument = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        if(!approvalDocument.isSameDrafter(userId)) {
            throw new BusinessException(ErrorCode.NOT_DRAFTER);
        }

        if(!approvalDocument.isTempStatus()) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_DOCUMENT);
        }

        return approvalDocument;
    }

    private ApprovalDocument validateDeleteAuthority(Long userId, Long docId) {
        ApprovalDocument approvalDocument = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        if(!approvalDocument.isSameDrafter(userId)) {
            throw new BusinessException(ErrorCode.NOT_DRAFTER);
        }

        if(!approvalDocument.isTempStatus()) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_DOCUMENT);
        }

        return approvalDocument;
    }

    private void validateApproverList(List<Long> approvalIds) {
        if (approvalIds == null || approvalIds.isEmpty()) {
            throw new BusinessException(ErrorCode.APPROVER_REQUIRED);
        }

        if (approvalIds.size() != 3) {
            throw new BusinessException(ErrorCode.INVALID_APPROVER_COUNT);
        }
    }

    private void createApprovalLines(Long docId, List<Long> approvalIds) {

        for(int i = 0; i < approvalIds.size(); i++) {
            ApprovalLine newLine = ApprovalLine.builder()
                    .document(docId)
                    .approver(approvalIds.get(i))
                    .lineStatus(LineStatusEnum.WAIT)
                    .sequence(i + 1)
                    .build();

            approvalLineRepository.save(newLine);
        }
    }
}
