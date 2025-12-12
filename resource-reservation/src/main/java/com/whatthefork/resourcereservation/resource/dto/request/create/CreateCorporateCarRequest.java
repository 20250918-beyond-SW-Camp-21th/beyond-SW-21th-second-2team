package com.whatthefork.resourcereservation.resource.dto.request.create;

import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateCorporateCarRequest (

        @NotBlank
        String name,

        @NotBlank
        String carNumber,

        @Min(2)
        int maxCapacity
) {
    public CreateCorporateCarRequest(CorporateCar carRequest) {
        this(
                carRequest.getName(),
                carRequest.getCarNumber(),
                carRequest.getMaxCapacity()
                );
    }
}
