package com.whatthefork.userservice.command.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    private final String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private final String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private final String password;

    private final int positionCode;

    @NotBlank(message = "부서 ID는 필수입니다")
    private final String deptId;

    private final boolean isDeptLeader;
    private final boolean isAdmin;
}
