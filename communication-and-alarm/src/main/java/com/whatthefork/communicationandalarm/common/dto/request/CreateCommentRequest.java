package com.whatthefork.communicationandalarm.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class CreateCommentRequest {

    Long parentCommentId;

    @NotBlank(message = "댓글을 작성해 주세요.")
    String content;
}


