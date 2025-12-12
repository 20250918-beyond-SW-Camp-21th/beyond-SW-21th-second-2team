package com.whatthefork.resourcereservation.resource.dto.response;

public record ReservationAndConferenceRoom(

        ReservationResponse reservationResponse,
        ConferenceRoomResponse conferenceRoomResponse
) {
}
