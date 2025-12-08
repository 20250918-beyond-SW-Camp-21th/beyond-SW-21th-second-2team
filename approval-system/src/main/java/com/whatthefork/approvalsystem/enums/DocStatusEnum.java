package com.whatthefork.approvalsystem.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocStatusEnum {

    TEMP("임시 저장"),
    IN_PROGRESS("결재 진행중"),
    APPROVED("최종 승인 완료"),
    REJECTED("반려됨");

    private final String description;
}