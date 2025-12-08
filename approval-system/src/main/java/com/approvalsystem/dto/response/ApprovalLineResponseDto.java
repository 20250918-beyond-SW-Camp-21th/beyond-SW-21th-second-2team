package com.approvalsystem.dto.response;

import com.approvalsystem.enums.LineStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ApprovalLineResponseDto {

    private Long approverId;
    private String approverName;
    private int sequence;
    private LineStatusEnum status;
    private LocalDateTime approvedAt;

}
