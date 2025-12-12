package com.whatthefork.approvalsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class UpdateDocumentRequestDto {

    @NotNull(message = "결재선 목록은 필수입니다.")
    @Size(min = 3, max = 3, message = "결재선은 필수로 3명을 지정해야 합니다.")
    private List<Long> approverIds;

    @Size(max = 5)
    private List<Long> referenceIds;

    @NotBlank(message = "제목은 필수로 입력해야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수로 입력해야 합니다.")
    private String content;

    @NotNull(message = "휴가 시작 일자는 필수로 지정해야 합니다.")
    private LocalDate startVacationDate;

    @NotNull(message = "휴가 종료 일자는 필수로 지정해야 합니다.")
    private LocalDate endVacationDate;

    @NotBlank(message = "수정 사유는 필수로 입력해야 합니다.")
    private String updateComment;

}
