package com.whatthefork.resourcereservation.resource.dto.request.update;

import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import com.whatthefork.resourcereservation.resource.entity.Supplies;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSuppliesRequest (

        @NotBlank
        String name,

        @Min(1)
        int capacity,

        @NotNull
        boolean isBooked
) {
    public UpdateSuppliesRequest(Supplies supplies) {
        this(
                supplies.getName(),
                supplies.getCapacity(),
                supplies.isBooked()
        );
    }
}
