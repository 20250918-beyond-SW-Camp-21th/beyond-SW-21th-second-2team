package com.ohgiraffers.communicationandalarm.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCommentRequest {

    private Long commentId;

    @NotBlank(message = "댓글을 작성해주세요.")
    private String content;
}
