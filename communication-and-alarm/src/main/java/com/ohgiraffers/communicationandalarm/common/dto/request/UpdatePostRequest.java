package com.ohgiraffers.communicationandalarm.common.dto.request;

import com.ohgiraffers.communicationandalarm.common.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePostRequest {

    @NotNull
    private Category category;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
