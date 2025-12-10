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
    ATTENDANCE_NOT_CHECK_IN_TIME(HttpStatus.BAD_REQUEST, "A004","출근 가능한 시간이 아닙니다."),

    ANNUAL_LEAVE_NOT_FOUND(HttpStatus.NOT_FOUND, "A100", "해당 연도의 연차 정보가 존재하지 않습니다."),
    ANNUAL_LEAVE_INSUFFICIENT(HttpStatus.BAD_REQUEST, "A101", "잔여 연차가 부족합니다."),
    INVALID_ANNUAL_LEAVE_USAGE(HttpStatus.BAD_REQUEST, "A102", "차감할 연차 일수는 0보다 커야 합니다."),
    ANNUAL_LEAVE_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "A103", "종료일은 시작일보다 빠를 수 없습니다."),
    ANNUAL_LEAVE_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "A104", "연차 사용 이력을 찾을 수 없습니다."),
    ANNUAL_LEAVE_ALREADY_PROCESSED(HttpStatus.CONFLICT, "A105", "해당 연차 승인 요청은 이미 처리되었습니다."),
    INVALID_ANNUAL_LEAVE_APPROVAL_REQUEST(HttpStatus.BAD_REQUEST, "A106", "잘못된 연차 승인 요청입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
