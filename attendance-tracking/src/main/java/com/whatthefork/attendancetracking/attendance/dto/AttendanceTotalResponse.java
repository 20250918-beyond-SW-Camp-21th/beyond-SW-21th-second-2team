package com.whatthefork.attendancetracking.attendance.dto;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AttendanceTotalResponse {

    private Integer totalAttendanceCount;
    private Integer countIsLate;
    private Integer totalLateMinutes;
    private Integer totalOverTimeMinutes;


}
