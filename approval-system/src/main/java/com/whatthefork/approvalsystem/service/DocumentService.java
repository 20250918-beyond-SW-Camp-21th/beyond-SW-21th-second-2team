package com.whatthefork.approvalsystem.service;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.common.error.BusinessException;
import com.whatthefork.approvalsystem.common.error.ErrorCode;
import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.domain.ApprovalHistory;
import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.domain.ApprovalReferrer;
import com.whatthefork.approvalsystem.domain.member.MemberRepository;
import com.whatthefork.approvalsystem.dto.request.CreateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.request.UpdateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.response.ApprovalLineResponseDto;
import com.whatthefork.approvalsystem.dto.response.DocumentDetailResponseDto;
import com.whatthefork.approvalsystem.dto.response.DocumentListResponseDto;
import com.whatthefork.approvalsystem.dto.response.ReferrerResponseDto;
import com.whatthefork.approvalsystem.enums.ActionTypeEnum;
import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import com.whatthefork.approvalsystem.enums.LineStatusEnum;
import com.whatthefork.approvalsystem.feign.client.UserFeignClient;
import com.whatthefork.approvalsystem.feign.dto.UserDetailResponse;
import com.whatthefork.approvalsystem.repository.ApprovalDocumentRepository;
import com.whatthefork.approvalsystem.repository.ApprovalHistoryRepositoy;
import com.whatthefork.approvalsystem.repository.ApprovalLineRepository;
import com.whatthefork.approvalsystem.repository.ApprovalReferrerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ApprovalDocumentRepository approvalDocumentRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalHistoryRepositoy approvalHistoryRepositoy;
    private final ApprovalReferrerRepository approvalReferrerRepository;
    private final MemberRepository memberRepository;
    private final UserFeignClient userFeignClient;

    @Transactional
    public Long createDocument(String memberIdStr, CreateDocumentRequestDto requestDto) {

        Long memberId = Long.parseLong(memberIdStr);
        String drafterName = getUserName(memberId);

        // 결재선 저장
        List<Long> approvalIds = requestDto.getApproverIds();
        validateApproverList(approvalIds, memberId);

        // 기안 작성
        ApprovalDocument approvalDocument = ApprovalDocument.builder()
                        .drafter(memberId)
                        .drafterName(drafterName)
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
        Long docId = savedDoc.getId();

        // 결재선 등록
        createApprovalLines(docId, approvalIds);

        // 참조자 설정
        if(requestDto.getReferrer() != null) {
            for(Long referrerId : requestDto.getReferrer()) {
                ApprovalReferrer approvalReferrer = ApprovalReferrer.builder()
                        .document(docId)
                        .referrer(referrerId)
                        .viewedAt(null)
                        .build();

                approvalReferrerRepository.save(approvalReferrer);
            }
        }

        // 결재 로그 저장
        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actorName(getUserName(memberId))
                .actionType(ActionTypeEnum.CREATE)
                .build();

        approvalHistoryRepositoy.save(approvalHistory);

        return savedDoc.getId();
    }

    @Transactional
    public void updateDocument(Long memberId, Long docId, UpdateDocumentRequestDto requestDto) {
        List<Long> approvalIds = requestDto.getApproverIds();
        validateApproverList(approvalIds, memberId);

        ApprovalDocument approvalDocument = validateUpdateAuthority(memberId, docId);

        approvalDocument.updateDocument(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getStartVacationDate(),
                requestDto.getEndVacationDate()
        );

        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actorName(getUserName(memberId))
                .actionType(ActionTypeEnum.UPDATE)
                .comment(requestDto.getUpdateComment())
                .build();

        approvalHistoryRepositoy.save(approvalHistory);

        approvalLineRepository.deleteByDocumentId(docId);
        createApprovalLines(docId, approvalIds);
    }

    @Transactional
    public void deleteDocument(Long userId, Long docId) {
        ApprovalDocument approvalDocument = validateDeleteAuthority(userId, docId);
        approvalDocument.deleteDocument();
    }

    @Transactional(readOnly = true)
    public DocumentDetailResponseDto readDetailDocument(String memberIdStr, Long docId) {
        Long memberId = Long.parseLong(memberIdStr);

       ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

       /* 문서 아이디로 결재선과 참조자 목록을 모두 가져옴 */
       List<ApprovalLine> approvalLines = approvalLineRepository.findByDocumentOrderBySequence(docId);
       List<ApprovalReferrer> referrers = approvalReferrerRepository.findByDocumentOrderByViewedAt(docId);

        boolean isDrafter = document.isSameDrafter(memberId);
        boolean isApprover = approvalLines.stream().anyMatch(line -> line.getApprover().equals(memberId));
        boolean isReferrer = referrers.stream().anyMatch(ref -> ref.getReferrer().equals(memberId));

        if (!isDrafter && !isApprover && !isReferrer) {
            throw new BusinessException(ErrorCode.NO_READ_AUTHORIZATION);
        }

        List<ApprovalLineResponseDto> approvalLinesResponseDto = approvalLines.stream()
                .map(line -> ApprovalLineResponseDto.builder()
                        .approverId(line.getApprover())
                        .approverName(getUserName(line.getApprover()))
                        .sequence(line.getSequence())
                        .status(line.getLineStatus())
                        .approvedAt(line.getApprovedAt())
                        .build()
                )
                .toList();

        List<ReferrerResponseDto> referrerResponseDto = referrers.stream().map(
                referrer -> ReferrerResponseDto.builder()
                        .referrerId(referrer.getReferrer())
                        .referrerName(getUserName(referrer.getReferrer()))
                        .viewedAt(referrer.getViewedAt())
                        .build()
        ).toList();

        return DocumentDetailResponseDto.builder()
                .documentId(docId)
                .title(document.getTitle())
                .content(document.getContent())
                .docStatus(document.getDocStatus())
                .drafterId(document.getDrafter())
                .drafterName(getUserName(document.getDrafter()))
                .startVacationDate(document.getStartVacationDate())
                .endVacationDate(document.getEndVacationDate())
                .createdAt(document.getCreatedAt())
                .approvers(approvalLinesResponseDto)
                .referrers(referrerResponseDto)
                .build();
    }

    @Transactional
    public void writeReadHistory(Long docId, String memberIdStr) {

        Long memberId = Long.valueOf(memberIdStr);

        // 문서가 존재하는지
        ApprovalDocument approvalDocument = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        // 기안자 본인이면 로그 기록 하지 않음
        if(approvalDocument.isSameDrafter(memberId)) {
            return;
        }

        // 이 사람이 결재자인지
        boolean isApprover = approvalLineRepository.existsByDocumentAndApprover(docId, memberId);

        // 이 사람이 참조자인지, 참조자가 아니면 null 반환
        ApprovalReferrer approvalReferrer = approvalReferrerRepository.findByDocumentAndReferrer(docId, memberId)
                .orElse(null);

        // 둘다 아니면 에러 처리
        if(!isApprover && approvalReferrer == null) {
            throw new BusinessException(ErrorCode.NO_READ_AUTHORIZATION);
        }

        // 참조자일 경우 읽은 시간 설정
        if(approvalReferrer != null) {
            approvalReferrer.updateViewedAt();
        }

        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actorName(getUserName(memberId))
                .actionType(ActionTypeEnum.READ)
                .build();
        approvalHistoryRepositoy.save(approvalHistory);
    }

    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getTempDocumentList(Long memberId, Pageable pageable) {
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findByDocStatusAndDrafter(DocStatusEnum.TEMP, memberId, pageable);

        return getResponseDto(documentList);
    }

    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getProgressDocumentList(Long memberId, Pageable pageable) {
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findByDocStatusAndDrafter(DocStatusEnum.IN_PROGRESS, memberId, pageable);

        return getResponseDto(documentList);
    }

    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getClosedDocumentList(Long memberId, Pageable pageable) {
        List<DocStatusEnum> statuses = Arrays.asList(DocStatusEnum.APPROVED, DocStatusEnum.REJECTED);
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findByDrafterAndDocStatusIn(memberId, statuses, pageable);

        return getResponseDto(documentList);
    }

    /* 결재 대기함 (자신이 결재해야 될 문서 목록) */
    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getDocumentsToApprove(Long memberId, Pageable pageable) {

        Page<ApprovalDocument> documentList = approvalDocumentRepository.findDocumentsToApprove(memberId, pageable);
        return getResponseDto(documentList);
    }

    /* 기결재함(자신이 승인/반려 처리 한 문서 목록) */
    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getProcessedDocuments(Long memberId, Pageable pageable) {

        Page<ApprovalDocument> documentList = approvalDocumentRepository.findProcessedDocuments(memberId, pageable);
        return getResponseDto(documentList);
    }

    /* 참조 문서함(참조자로 지정된 문서 목록) */
    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getReferencedDocuments(Long  memberId, Pageable pageable) {

        Page<ApprovalDocument> documentList = approvalDocumentRepository.findReferencedDocuments(memberId, pageable);
        return getResponseDto(documentList);

    }

    private Page<DocumentListResponseDto> getResponseDto(Page<ApprovalDocument> documentList) {

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

    private void validateApproverList(List<Long> approvalIds, Long memberId) {
        if (approvalIds == null || approvalIds.isEmpty()) {
            throw new BusinessException(ErrorCode.APPROVER_REQUIRED);
        }

        if (approvalIds.size() != 3) {
            throw new BusinessException(ErrorCode.INVALID_APPROVER_COUNT);
        }

        // 기안자 = 결재자 방지
        if (approvalIds.stream().anyMatch(approverId -> approverId.equals(memberId))) {
            throw new BusinessException(ErrorCode.DRAFTER_EQUALS_APPROVER);
        }

        long existingApproverCount = memberRepository.countAllByIdIn(approvalIds);

        if (existingApproverCount != approvalIds.size()) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    private void createApprovalLines(Long docId, List<Long> approvalIds) {

        if (!approvalDocumentRepository.existsById(docId)) {
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

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

    private String getUserName(Long userId) {

        try {
            ApiResponse<UserDetailResponse> response = userFeignClient.findUserDetail(userId);

            if(response != null && response.getData() != null && response.getData().getUser() != null) {
                return response.getData().getUser().getName();
            }
            return "알 수 없는 유저입니다.";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "알 수 없는 유저입니다.";
        }
    }
}
