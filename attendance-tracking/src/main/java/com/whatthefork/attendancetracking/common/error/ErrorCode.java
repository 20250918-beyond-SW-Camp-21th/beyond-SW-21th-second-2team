package com.whatthefork.attendancetracking.common.error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //
    ATTENDANCE_ALREADY_CHECKED_IN(HttpStatus.BAD_REQUEST, "A001","이미 출근을 찍었습니다."),
    ATTENDANCE_ALREADY_CHECKED_OUT(HttpStatus.BAD_REQUEST, "A002","이미 퇴근을 찍었습니다."),
    ATTENDANCE_NOT_CHECKED_IN(HttpStatus.BAD_REQUEST,"A003","출근한 기록이 없습니다."),
    ATTENDANCE_NOT_CHECK_IN_TIME(HttpStatus.BAD_REQUEST, "A004","출근 가능한 시간이 아닙니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
