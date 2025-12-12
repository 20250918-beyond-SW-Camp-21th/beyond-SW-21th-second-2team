package com.whatthefork.resourcereservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 자원 없음
    RESOURCE_NOT_FOUND("10001", "해당 자원 찾을 수 없음", HttpStatus.NOT_FOUND),

    // 중복 예약
    REDUNDANT_RESERVATION("10002", "해당 자원은 이미 예약되어 있음", HttpStatus.CONFLICT),

    // 권한 부족
    NOT_ENOUGH_AUTHORITY("10003", "권한 부족", HttpStatus.UNAUTHORIZED),

    // 비품 개수 부족
    NOT_ENOUGH_CAPACITY("10004", "비품 개수 부족", HttpStatus.BAD_REQUEST);

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
