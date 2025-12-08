package com.whatthefork.resourcereservation.resource.dto.request.update;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateConferenceRoomRequest (

        @NotBlank
        String name,

        @Min(2)
        int maxCapacity,

        @NotNull
        boolean isBooked
) {
    public UpdateConferenceRoomRequest(ConferenceRoom conferenceRoom) {
        this(
                conferenceRoom.getName(),
                conferenceRoom.getMaxCapacity(),
                conferenceRoom.isBooked()
        );
    }
}