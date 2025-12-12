package com.whatthefork.attendancetracking.annualLeave.dto;

import jakarta.validation.constraints.NotNull;
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
