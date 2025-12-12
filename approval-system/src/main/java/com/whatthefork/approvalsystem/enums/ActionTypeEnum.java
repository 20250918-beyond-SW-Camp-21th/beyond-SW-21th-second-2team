package com.whatthefork.approvalsystem.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* 기안 상태 */
@Getter
@RequiredArgsConstructor
public enum ActionTypeEnum {

    CREATE("기안 작성"),
    SUBMIT("상신"),
    READ("열람"),
    APPROVE("승인"),
    REJECT("반려"),
    UPDATE("수정"),
    CANCEL("상신 취소");

    private final String description;
}