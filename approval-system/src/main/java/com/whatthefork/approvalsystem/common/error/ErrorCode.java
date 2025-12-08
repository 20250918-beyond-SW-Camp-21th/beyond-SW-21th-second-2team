package com.whatthefork.approvalsystem.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002","서버 내부 오류가 발생했습니다."),

    // Document
    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "해당 문서를 찾을 수 없습니다."),
    CANNOT_MODIFY_DOCUMENT(HttpStatus.BAD_REQUEST, "D002", "임시저장 상태의 문서만 수정할 수 있습니다."),
    CANNOT_DELETE_DOCUMENT(HttpStatus.BAD_REQUEST, "D003", "이미 결재가 진행된 문서는 삭제할 수 없습니다."),
    APPROVER_REQUIRED(HttpStatus.BAD_REQUEST, "D004", "결재자를 지정해야 합니다."),
    INVALID_APPROVER_COUNT(HttpStatus.BAD_REQUEST, "D005", "결재자는 반드시 3명이어야 합니다."),

    // Auth
    NOT_DRAFTER(HttpStatus.FORBIDDEN, "A001", "본인의 문서만 수정/삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
