package com.whatthefork.resourcereservation.resource.dto.response;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CorporateCarResponse(

        @NotNull
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String carNumber,

        @Min(2)
        int maxCapacity,

        @NotNull
        boolean isBooked
) {
    public CorporateCarResponse(CorporateCar corporateCar) {
        this(
                corporateCar.getId(),
                corporateCar.getName(),
                corporateCar.getCarNumber(),
                corporateCar.getMaxCapacity(),
                corporateCar.isBooked()
        );
    }
}