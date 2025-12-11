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
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "해당 사용자를 찾을 수 없습니다."),

    // Document
    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "해당 문서를 찾을 수 없습니다."),
    CANNOT_MODIFY_DOCUMENT(HttpStatus.BAD_REQUEST, "D002", "임시저장 상태의 문서만 수정할 수 있습니다."),
    CANNOT_DELETE_DOCUMENT(HttpStatus.BAD_REQUEST, "D003", "이미 결재가 진행된 문서는 삭제할 수 없습니다."),
    APPROVER_REQUIRED(HttpStatus.BAD_REQUEST, "D004", "결재자를 지정해야 합니다."),
    INVALID_APPROVER_COUNT(HttpStatus.BAD_REQUEST, "D005", "결재자는 반드시 3명이어야 합니다."),
    DRAFTER_EQUALS_APPROVER(HttpStatus.BAD_REQUEST, "D006", "본인은 결재자로 등록할 수 없습니다."),
    NOT_FOUND_TO_APPROVE(HttpStatus.NOT_FOUND, "D007", "결재할 문서가 없습니다."),

    // Approval
    CANNOT_CANCEL_SUBMIT(HttpStatus.BAD_REQUEST, "P001", "결재자가 읽은 문서는 상신을 취소할 수 없습니다."),
    NOT_MATCH_APPROVER(HttpStatus.BAD_REQUEST, "P002", "해당 문서의 결재자만 기안을 열람할 수 있습니다."),
    NOT_MATCH_REFERRER(HttpStatus.BAD_REQUEST, "P003", "해당 문서의 참조자만 기안을 열람할 수 있습니다."),
    CANNOT_APPROVE(HttpStatus.BAD_REQUEST, "P004", "결재할 권한이 없습니다."),
    ALREADY_PROCESS(HttpStatus.BAD_REQUEST, "P005", "이미 결재 및 반려한 문서입니다."),

    // Auth
    NOT_DRAFTER(HttpStatus.FORBIDDEN, "A001", "본인의 문서만 수정/삭제할 수 있습니다."),
    NO_READ_AUTHORIZATION(HttpStatus.FORBIDDEN, "A002", "문서 열람의 권한이 없습니다."),

    // Client
    ANNUAL_LEAVE_FAILURE(HttpStatus.BAD_REQUEST, "C001", "Feign Client 전송에 실패하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
