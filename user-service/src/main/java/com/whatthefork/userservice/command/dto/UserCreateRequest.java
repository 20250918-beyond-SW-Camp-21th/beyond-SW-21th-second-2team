package com.whatthefork.userservice.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateRequest {
    private final String name;
    private final String email;
    private final String password;
    private final int positionCode;
    private final String deptId;
    private final boolean isDeptLeader;
    private final boolean isAdmin;
}
