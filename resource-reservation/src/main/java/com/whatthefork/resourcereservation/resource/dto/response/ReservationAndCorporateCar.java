package com.whatthefork.resourcereservation.resource.dto.response;

public record ReservationAndCorporateCar (

        ReservationResponse reservationResponse,
        CorporateCarResponse corporateCarResponse
) {

}
