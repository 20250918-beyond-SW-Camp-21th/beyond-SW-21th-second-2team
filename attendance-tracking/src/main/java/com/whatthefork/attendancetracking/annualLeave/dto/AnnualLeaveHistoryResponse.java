package com.whatthefork.attendancetracking.annualLeave.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AnnualLeaveHistoryResponse {
    private Long id;

    private Long memberId;

    private Integer usedLeave ;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long approverId;

    private LocalDateTime createdAt;
}
