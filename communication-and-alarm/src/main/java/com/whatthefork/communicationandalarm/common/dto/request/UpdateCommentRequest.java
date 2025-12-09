package com.whatthefork.communicationandalarm.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCommentRequest {

    @NotBlank(message = "댓글을 수정해주세요.")
    private String content;
}
