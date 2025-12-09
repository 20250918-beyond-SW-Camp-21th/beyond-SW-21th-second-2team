package com.whatthefork.userservice.command.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private final String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    private final String newPassword;
}