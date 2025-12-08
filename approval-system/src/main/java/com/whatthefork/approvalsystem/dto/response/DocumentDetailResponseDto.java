package com.whatthefork.approvalsystem.dto.response;

import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DocumentDetailResponseDto {

    // 문서 기본 정보
    private Long documentId;
    private String title;
    private String content;
    private DocStatusEnum docStatus;

    // 기안자 정보
    private Long drafterId;

    // 날짜 정보
    private LocalDate startVacationDate;
    private LocalDate endVacationDate;
    private LocalDateTime createdAt;

    // 결재자, 참조자
    private List<ApprovalLineResponseDto> approvers;
    private List<ReferrerResponseDto> referrers;

}
