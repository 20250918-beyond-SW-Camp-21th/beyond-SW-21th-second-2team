package com.whatthefork.resourcereservation.resource.dto.request;

import com.whatthefork.resourcereservation.resource.entity.CanceledReservation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CanceledReservationRequest(

        @NotNull
        Long id,

        @NotBlank
        String reason,

        @NotNull
        LocalDateTime canceledDate
) {
    public CanceledReservationRequest(CanceledReservation reservation) {
        this(
                reservation.getId(),
                reservation.getReason(),
                reservation.getCanceledDate()
        );
    }
}
