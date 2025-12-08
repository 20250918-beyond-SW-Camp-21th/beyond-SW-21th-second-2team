package com.whatthefork.approvalsystem.dto.response;

import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DocumentListResponseDto {

    private Long documentId;
    private String title;
    private DocStatusEnum status;
    private LocalDateTime createdDate;

}
