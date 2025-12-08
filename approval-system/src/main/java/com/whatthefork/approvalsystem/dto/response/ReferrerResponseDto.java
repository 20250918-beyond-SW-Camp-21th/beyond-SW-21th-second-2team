package com.whatthefork.approvalsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ReferrerResponseDto {

    private Long referrerId;
    private String referrerName;
    private LocalDateTime viewedAt;
}
