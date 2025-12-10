package com.whatthefork.approvalsystem.service;

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
        validateApproverList(approvalIds, memberId);

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
    public DocumentDetailResponseDto readDetailDocument(Long memberId, Long docId) {
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

        return DocumentDetailResponseDto.builder()
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
    }

    //     기안자, 결재자, 참조자 아니면 읽지 못 하게.
//     그 후 ApprovalHistory에 action은 결재자, READ로 변경하고 읽은 시간도 추가하는 로그를 남기는 메소드 생성
    //  ActionTypeEnum.READ는 새로운 로그를 추가하는 행위 (상태 변경 아님)
//  ApprovalHistory: READ 로그 기록. (문서 열람 시간 추적)
//  ApprovalReferrer: 참조자가 열람 시 viewedAt 필드를 현재 시간으로 업데이트.
//  ApprovalLine: 결재자가 열람해도 LineStatus는 WAIT 상태 유지.
    @Transactional
    public void writeReadHistory(Long docId, Long memberId) {

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

        // 우선 전체 문서중, 멤버ID가 포함되고 그 포함된 결재선의 시퀀스와 문서의 시퀀스가 같은 것들의 문서를 불러와야함
        // 그럼 그 문서를 ResponseDTO에 담아서 return
        // 접근 권한 넣기
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findDocumentsToApprove(memberId, pageable);
        return getResponseDto(documentList);
    }

    /* 기결재함(자신이 승인/반려 처리 한 문서 목록) */
    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getProcessedDocuments(Long memberId, Pageable pageable) {

        /*
        * 1. History의 기안 id = Document의 기안 id
        * 2. History의 actor = memberId
        * 3. History에 타입이 APPROVE 혹은 REJECT 인것
        * 4. Document에 TEMP만 아니면 됨
        * */
        Page<ApprovalDocument> documentList = approvalDocumentRepository.findProcessedDocuments(memberId, pageable);
        return getResponseDto(documentList);
    }

    /* 참조 문서함(참조자로 지정된 문서 목록) */
    @Transactional(readOnly = true)
    public Page<DocumentListResponseDto> getReferencedDocuments(Long  memberId, Pageable pageable) {
        /*
        * 1. Referrer의 기안 id = Document의 기안 id
        * 2. Refferer의 referrer = memberId
        * 3.
        * */
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
}
