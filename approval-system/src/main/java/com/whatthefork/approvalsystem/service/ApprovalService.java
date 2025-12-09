package com.whatthefork.approvalsystem.service;

import com.whatthefork.approvalsystem.common.error.BusinessException;
import com.whatthefork.approvalsystem.common.error.ErrorCode;
import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.domain.ApprovalHistory;
import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.dto.response.DocumentListResponseDto;
import com.whatthefork.approvalsystem.enums.ActionTypeEnum;
import com.whatthefork.approvalsystem.enums.LineStatusEnum;
import com.whatthefork.approvalsystem.repository.ApprovalDocumentRepository;
import com.whatthefork.approvalsystem.repository.ApprovalHistoryRepositoy;
import com.whatthefork.approvalsystem.repository.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalDocumentRepository approvalDocumentRepository;
    private final ApprovalHistoryRepositoy approvalHistoryRepositoy;
    private final ApprovalLineRepository approvalLineRepository;

/* 상신 (기안자)
 권한: 기안자만 가능하며, DocStatus가 TEMP일 때만 허용
 ApprovalDocument: DocStatus 변경 (TEMP -> IN_PROGRESS), CurrentSequence = 1로 설정.
 ApprovalHistory: SUBMIT 로그 기록 (기존 CREATE 로그는 유지)
 상신 취소 : 첫 번째 결재자가 처리 전이라면 IN_PROGRESS -> TEMP로 복귀 가능.*/
    @Transactional
    public void submitApproval(Long docId, Long memberId) {
        ApprovalDocument document = validateSubmitAuthority(docId, memberId);

        document.updateProgress(); // TEMP -> IN_PROGRESS

        approvalLineRepository.updateLineStatusByDocumentAndSequence(docId, 1, LineStatusEnum.WAIT); // 첫번째 결재자의 상태를 WAIT으로 상태(명시적으로 한 번 다시 쓰기)

        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actionType(ActionTypeEnum.SUBMIT)
                .build();

        approvalHistoryRepositoy.save(approvalHistory);
    }

    @Transactional
    public void cancelSubmit(Long docId, Long memberId) {
        ApprovalDocument document = validateSubmitAuthority(docId, memberId);

        ApprovalLine firstLine = approvalLineRepository.findByDocumentAndSequence(docId, 1)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPROVER_REQUIRED));

        // 첫번째 결재자의 memberId 가져옴
        Long firstLineApproverId = firstLine.getApprover();

        // 첫번째 라인의 상태가 WAIT일 경우(APPROVED나 REJECT면 X)
        if(!firstLine.getLineStatus().equals(LineStatusEnum.WAIT)) {
            throw new  BusinessException(ErrorCode.CANNOT_CANCEL_SUBMIT);
        }

        // 첫번째 결재자가 해당 문서의 로그에 READ를 남긴 적이 있다면
        boolean isRead = approvalHistoryRepositoy.existsByDocumentAndActorAndActionType(docId, firstLineApproverId, ActionTypeEnum.READ);

        if(isRead) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_SUBMIT);
        }

        // 상신 취소. IN_PROGRESS -> TEMP
        document.updateTemp();

        // 로그에 상신취소 남기기
        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actionType(ActionTypeEnum.CANCEL)
                .build();
        approvalHistoryRepositoy.save(approvalHistory);

        }

    @Transactional
    public void approveDocument(Long docId, Long memberId) {
        /*
        * 1. document에서 현재 시퀀스를 가져와 line의 sequence로 currentsequence에 해당하는 멤버를 불러온다. (lineRepository 활용)
        * 2. 현재 결재자의 LineStatus를 APPROVED로 변경하고 ApprovalHistory에 기록한다.
        * 2.1. 마지막 결재자가 아니면 currentSequence를 1 증가 시키고, 다음 결재자의 LineStatus를 Wait으로 설정한다. (근데 createDocument에서 기본값이 다 wait이긴 한데)
        * 2.2. 최종 결재자일 경우 ApprovalDocument의 상태를 APPROVED로 변경한다.
        *
        * */

        // docId로 Document의 현재 sequence를 뽑아 내야함
        ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        int currentSequence = document.getCurrentSequence();

        ApprovalLine currentLine = validateApprovalLine(docId, document.getCurrentSequence(),  memberId);

        // LineStatus를 approve로 변경
        currentLine.approve();

        // 결재 로그에 Approve로 등록
        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actionType(ActionTypeEnum.APPROVE)
                .build();
        approvalHistoryRepositoy.save(approvalHistory);

        int nextSequence = currentSequence + 1;
        boolean hasNextApprover = approvalLineRepository.existsByDocumentAndSequence(docId, nextSequence);

        if(hasNextApprover) {
            document.nextSequence();
        } else {
            document.completeApproval();
            // annualRepository.findByMember(memberId)로 불러와서, annualLeave.decreaseAnnual((int) 휴가 종료일 - 휴가 시작일)
        }
    }

    @Transactional
    public void rejectDocument(Long docId, Long memberId) {
        /*
        * 1. Document 에서 현재 시퀀스를 가져와 line의 sequence로 currentsequence에 해당하는 멤버를 불러온다. (lineRepository 활용) -> 승인과 동일
        * 2. Line에서 Status WAIT에서 REJECTED로 변경 후 approvedAt 기록 (Builder 객체 생성 시 자동 부여)
        * 3. Document docStatus를 변경 (REJECTED) 후 결재 종료
        * 4. History REJECT 로그 기록
        * */
        ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        ApprovalLine currentLine = validateApprovalLine(docId, document.getCurrentSequence(),  memberId);

        currentLine.reject();

        document.rejectApproval();

        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actionType(ActionTypeEnum.REJECT)
                .build();
        approvalHistoryRepositoy.save(approvalHistory);
    }

    public ApprovalDocument validateSubmitAuthority(Long docId, Long memberId) {
        ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        if(!memberId.equals(document.getDrafter())) {
            throw new BusinessException(ErrorCode.NOT_DRAFTER);
        }

        return document;
    }

    public ApprovalLine validateApprovalLine(Long docId, int currentSequence, Long memberId) {
        // 현재 문서의 결재선의 sequence가 현재 문서의 CurrentSequence인 approver를 찾아내야 함
        // 전달 받은 결재자와 결재선의 sequence에 있는 결재자가 다를 경우
        ApprovalLine currentLine = approvalLineRepository.findByDocumentAndSequenceAndApprover(docId, currentSequence, memberId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_MATCH_APPROVER)
        );

        // WAIT 상태가 아닌 경우 이미 처리를 한 것임
        if(currentLine.getLineStatus() != LineStatusEnum.WAIT) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESS);
        }

        return currentLine;
    }
}
