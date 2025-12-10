package com.whatthefork.approvalsystem.feign.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LeaveAnnualRequestDto {
    private Long memberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long approverId;
}
