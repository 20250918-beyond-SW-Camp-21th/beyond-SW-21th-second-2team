package com.whatthefork.resourcereservation.resource.dto.request.update;

import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCorporateCarRequest (

        @NotBlank
        String name,

        @NotBlank
        String carNumber,

        @Min(2)
        int maxCapacity,

        @NotNull
        boolean isBooked
) {
    public UpdateCorporateCarRequest(CorporateCar carRequest) {
        this(
                carRequest.getName(),
                carRequest.getCarNumber(),
                carRequest.getMaxCapacity(),
                carRequest.isBooked()
        );
    }
}
