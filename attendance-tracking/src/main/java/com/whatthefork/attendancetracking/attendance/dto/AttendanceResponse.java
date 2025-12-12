package com.whatthefork.attendancetracking.attendance.dto;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class AttendanceResponse {

    private LocalDate date;
    private String punchIn;
    private String punchOut;
    private String workTime;

    public static AttendanceResponse from(Attendance attendance) {
        return AttendanceResponse.builder()
                .date(attendance.getPunchInDate().toLocalDate())
                .punchIn(formatPunchIn(attendance))
                .punchOut(formatPunchOut(attendance))
                .workTime(formatWorkTime(attendance))
                .build();
    }


    private static String formatPunchIn(Attendance attendance) {

        String punchIn = attendance.getPunchInDate().format(DateTimeFormatter.ofPattern("HH:mm"));

        if(attendance.isLate()){
            punchIn += "(지각: "+attendance.getLateMinutes() + "분)";
        }

        return punchIn;
    }

    private static String formatPunchOut(Attendance attendance) {

        if(attendance.getPunchOutDate()==null){
            return "-";
        }
        String punchOut = attendance.getPunchOutDate().format(DateTimeFormatter.ofPattern("HH:mm"));

        return punchOut;
    }

    private static String formatWorkTime(Attendance attendance) {

        if(attendance.getPunchOutDate()==null){
            return "-";
        }
        int totalMinutes = (int)Duration.between(attendance.getPunchInDate(),attendance.getPunchOutDate()).toMinutes();
        int minutes = totalMinutes%60;
        int hours = totalMinutes/60;
        String workTime = hours+"시간 "+minutes+"분";

        if(attendance.getOvertimeMinutes()>0){
            workTime += "(초과: "+attendance.getOvertimeMinutes()+"분)";
        }
        return workTime;
    }




}
