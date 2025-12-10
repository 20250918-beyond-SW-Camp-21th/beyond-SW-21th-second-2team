package com.whatthefork.attendancetracking.annualLeave.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnnualLeaveResponse {
    private Long memberId;

    private Integer year;

    private Integer totalLeave;

    private Integer usedLeave;

    private Integer remainingLeave;
}
