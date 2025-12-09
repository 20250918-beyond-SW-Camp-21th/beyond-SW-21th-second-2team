package com.whatthefork.attendancetracking.attendance.dto;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AttendanceTotalResponse {

    private String totalAttendanceCount;
    private String totalLate;
    private String totalOverTimeMinutes;


}
