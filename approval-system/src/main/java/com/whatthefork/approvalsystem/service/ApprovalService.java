package com.whatthefork.approvalsystem.service;

import com.whatthefork.approvalsystem.common.error.BusinessException;
import com.whatthefork.approvalsystem.common.error.ErrorCode;
import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.domain.ApprovalHistory;
import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.enums.ActionTypeEnum;
import com.whatthefork.approvalsystem.enums.LineStatusEnum;
import com.whatthefork.approvalsystem.repository.ApprovalDocumentRepository;
import com.whatthefork.approvalsystem.repository.ApprovalHistoryRepositoy;
import com.whatthefork.approvalsystem.repository.ApprovalLineRepository;
import com.whatthefork.approvalsystem.repository.ApprovalReferrerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalDocumentRepository approvalDocumentRepository;
    private final ApprovalHistoryRepositoy approvalHistoryRepositoy;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalReferrerRepository approvalReferrerRepository;

    // Submit (상신 -> 기안자)
    /* ========================================== 상신 =======================================================
    * 1. 기안자가 임시 저장 상태의 기안을 상신 버튼을 누름
    * 2. 첫번째 결재자 순서로 이동.
    *    DocStatus = CREATE -> SUBMIT,
    *    DocStatusEnum TEMP -> IN PROGRESS
    *   (첫 번째 결재자가 아직 안 읽었을 경우 상신 취소 가능. 그러면 SUBMIT -> CREATE)
    * ============================================ 읽음 =====================================================
    * 3. 첫번째 결재자가 readDetailDocument 하면 ActionType이 SUBMIT -> READ로 변경.
    * ============================================ 승인, 반려 =====================================================
    * 3.1 첫번째 결재자가 승인하면 ActionType READ -> APPROVE 후 해당 결재자의 LineStatus를 APPROVED로 변경 그 다음 결재자 순서로 넘김.
    * 3.2 첫번째 결재자가 반려하면 ActionType READ -> REJECT 후 DocStatus IN_PROGRESS -> REJECTED
    * 4. 세번째 결재자까지 반복. 세번째 결재자까지 승인하면 DocStatus IN_PROGRESS -> APPROVED
    * 5. 휴가 날짜 계산 로직 발동 (추후 구현)
    * */

/* 상신 (기안자)
 권한: 기안자만 가능하며, DocStatus가 TEMP일 때만 허용
 ApprovalDocument: DocStatus 변경 (TEMP -> IN_PROGRESS), CurrentSequence = 1로 설정.
 ApprovalHistory: SUBMIT 로그 기록 (기존 CREATE 로그는 유지)
 상신 취소 : 첫 번째 결재자가 처리 전이라면 IN_PROGRESS -> TEMP로 복귀 가능.*/
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

    public void cancleSubmit(Long docId, Long memberId) {
        ApprovalDocument document = validateSubmitAuthority(docId, memberId);

        ApprovalLine firstLine = approvalLineRepository.findByDocumentAndSequence(docId, 1)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPROVER_REQUIRED));

        // 첫번째 결재자의 memberId 가져옴
        Long firstLineApproverId = firstLine.getApprover();

        // 첫번째 라인의 상태가 WAIT일 경우(APPROVED나 REJECT면 X)
        if(!firstLine.getLineStatus().equals(LineStatusEnum.WAIT)) {
            throw new  BusinessException(ErrorCode.CANNOT_CANCLE_SUBMIT);
        }

        // 첫번째 결재자가 해당 문서의 로그에 READ를 남긴 적이 있다면
        boolean isRead = approvalHistoryRepositoy.existsByDocumentAndActorAndActionType(docId, firstLineApproverId, ActionTypeEnum.READ);

        if(isRead) {
            throw new BusinessException(ErrorCode.CANNOT_CANCLE_SUBMIT);
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

    public ApprovalDocument validateSubmitAuthority(Long docId, Long memberId) {
        ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        if(!memberId.equals(document.getDrafter())) {
            throw new BusinessException(ErrorCode.NOT_DRAFTER);
        }

        return document;
    }
}
