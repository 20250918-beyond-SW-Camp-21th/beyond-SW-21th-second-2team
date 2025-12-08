package com.approvalsystem.dto.response;

import com.approvalsystem.enums.DocStatusEnum;
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
