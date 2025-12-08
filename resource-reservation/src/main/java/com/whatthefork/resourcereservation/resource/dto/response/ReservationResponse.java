package com.whatthefork.resourcereservation.resource.dto.response;

import com.whatthefork.resourcereservation.resource.entity.Reservation;
import com.whatthefork.resourcereservation.resource.enums.ResourceCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDateTime;

public record ReservationResponse(

    @NotNull
    Long id,

    @NotNull
    Long userId,

    @NotNull
    Long resourceId,

    @Past
    LocalDateTime bookedDate,

    LocalDateTime startDate,
    LocalDateTime endDate,

    @Min(2)
    int capacity,

    @NotBlank
    String reason,

    @NotNull
    ResourceCategory category,

    @NotNull
    boolean isExpired
) {
    public ReservationResponse(Reservation management) {
        this(
                management.getId(),
                management.getUserId(),
                management.getResourceId(),
                management.getBookedDate(),
                management.getStartDate(),
                management.getEndDate(),
                management.getCapacity(),
                management.getReason(),
                management.getCategory(),
                management.isExpired()
        );
    }
}