package com.whatthefork.resourcereservation.resource.dto.response;

public record ReservationAndSupply(

        ReservationResponse reservationResponse,
        SuppliesResponse supplies
) {
}
