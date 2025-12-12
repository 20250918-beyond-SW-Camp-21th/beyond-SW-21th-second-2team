package com.whatthefork.resourcereservation.resource.dto.request.create;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.whatthefork.resourcereservation.resource.entity.Reservation;
import com.whatthefork.resourcereservation.resource.enums.ResourceCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationRequest(

        @NotNull(message = "사용할 자원을 선택해 주세요.")
        Long resourceId,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime bookedDate,

        @Future(message = "현재보다 나중 시각에만 예약이 가능합니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @Future(message = "현재보다 나중 시각에만 예약이 가능합니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate,

        @Min(value = 1, message = "반드시 1 이상 입력해야 합니다.")
        int capacity,

        @NotBlank(message = "사유를 반드시 입력하세요.")
        String reason,

        @NotNull(message = "카테고리를 입력해 주세요.")
        ResourceCategory category
) {
    public CreateReservationRequest(Reservation reservation) {
        this(
                reservation.getResourceId(),
                reservation.getBookedDate(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getCapacity(),
                reservation.getReason(),
                reservation.getCategory()
        );
    }
}
