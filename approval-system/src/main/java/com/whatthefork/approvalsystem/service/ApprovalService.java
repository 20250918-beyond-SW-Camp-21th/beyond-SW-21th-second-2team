package com.whatthefork.approvalsystem.service;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.common.error.BusinessException;
import com.whatthefork.approvalsystem.common.error.ErrorCode;
import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.domain.ApprovalHistory;
import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.enums.ActionTypeEnum;
import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import com.whatthefork.approvalsystem.enums.LineStatusEnum;
import com.whatthefork.approvalsystem.feign.client.AnnualLeaveFeignClient;
import com.whatthefork.approvalsystem.feign.client.UserFeignClient;
import com.whatthefork.approvalsystem.feign.dto.LeaveAnnualRequestDto;
import com.whatthefork.approvalsystem.feign.dto.UserDetailResponse;
import com.whatthefork.approvalsystem.repository.ApprovalDocumentRepository;
import com.whatthefork.approvalsystem.repository.ApprovalHistoryRepositoy;
import com.whatthefork.approvalsystem.repository.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalDocumentRepository approvalDocumentRepository;
    private final ApprovalHistoryRepositoy approvalHistoryRepositoy;
    private final ApprovalLineRepository approvalLineRepository;
    private final AnnualLeaveFeignClient annualLeaveFeignClient;
    private final UserFeignClient userFeignClient;

    /* 상신 (기안자) */
    @Transactional
    public void submitApproval(Long docId, Long memberId) {
        ApprovalDocument document = validateSubmitAuthority(docId, memberId);

        if(document.getDocStatus() !=  DocStatusEnum.TEMP) {
            throw new BusinessException(ErrorCode.ALREADY_SUBMIT);
        }
        // TEMP -> IN_PROGRESS
        document.updateProgress();

        // 첫번째 결재자의 상태를 WAIT으로 상태(명시적으로 한 번 다시 쓰기)
        approvalLineRepository.updateLineStatusByDocumentAndSequence(docId, 1, LineStatusEnum.WAIT);

        // 결재 로그 저장
        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actionType(ActionTypeEnum.SUBMIT)
                .actorName(getUserName(memberId))
                .build();

        approvalHistoryRepositoy.save(approvalHistory);
    }

    /* 상신 취소 (기안자) */
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
                .actorName(getUserName(memberId))
                .build();
        approvalHistoryRepositoy.save(approvalHistory);

        }

    /* 기안 결재 */
    @Transactional
    public void approveDocument(Long docId, Long memberId, String comment) {

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
                .actorName(getUserName(memberId))
                .comment(comment)
                .build();
        approvalHistoryRepositoy.save(approvalHistory);

        int nextSequence = currentSequence + 1;
        boolean hasNextApprover = approvalLineRepository.existsByDocumentAndSequence(docId, nextSequence);

        // 다음 결재선에 결재자가 존재한다면 시퀀스를 1 증가시키고,
        // 존재하지 않다면 문서 상태를 APPROVE로 변경하고 연차 삭감 처리
        if(hasNextApprover) {
            document.nextSequence();
        } else {
            document.completeApproval();
            try {
                LeaveAnnualRequestDto requestDto = LeaveAnnualRequestDto.builder()
                        .memberId(document.getDrafter())
                        .startDate(document.getStartVacationDate())
                        .endDate(document.getEndVacationDate())
                        .approverId(memberId)
                        .build();

                annualLeaveFeignClient.decreaseAnnualLeave(requestDto);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.ANNUAL_LEAVE_FAILURE);
            }
        }
    }

    /* 기안 반려 */
    @Transactional
    public void rejectDocument(Long docId, Long memberId, String comment) {
        ApprovalDocument document = approvalDocumentRepository.findById(docId).orElseThrow(
                () -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND)
        );

        ApprovalLine currentLine = validateApprovalLine(docId, document.getCurrentSequence(), memberId);

        // 결재선 상태를 REJECTED로 변경
        currentLine.reject();

        // 현재 문서의 상태를 REJECTED로 변경
        document.rejectApproval();

        ApprovalHistory approvalHistory = ApprovalHistory.builder()
                .document(docId)
                .actor(memberId)
                .actionType(ActionTypeEnum.REJECT)
                .actorName(getUserName(memberId))
                .comment(comment)
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
        ApprovalLine currentLine = approvalLineRepository.findByDocumentAndSequenceAndApprover(docId, currentSequence, memberId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_MATCH_APPROVER)
        );

        // WAIT 상태가 아닌 경우 이미 처리를 한 것임
        if(currentLine.getLineStatus() != LineStatusEnum.WAIT) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESS);
        }

        return currentLine;
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
