package com.whatthefork.approvalsystem.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LineStatusEnum {

    WAIT("결재 대기"),
    APPROVED("승인"),
    REJECTED("반려"),
    CANCELED("취소됨"); // 상신 취소

    private final String description;
}