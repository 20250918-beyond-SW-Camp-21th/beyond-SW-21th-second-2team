package com.whatthefork.approvalsystem.feign.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private Long id;
    private String name;
}
