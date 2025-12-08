package com.whatthefork.resourcereservation.resource.dto.response;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import com.whatthefork.resourcereservation.resource.entity.Supplies;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SuppliesResponse(

        @NotNull
        Long id,

        @NotBlank
        String name,

        @Min(1)
        int capacity,

        @NotNull
        boolean isBooked
) {
    public SuppliesResponse(Supplies supplies) {
        this(
                supplies.getId(),
                supplies.getName(),
                supplies.getCapacity(),
                supplies.isBooked()
        );
    }
}