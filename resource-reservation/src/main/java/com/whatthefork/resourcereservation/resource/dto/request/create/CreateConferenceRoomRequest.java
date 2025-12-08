package com.whatthefork.resourcereservation.resource.dto.request.create;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateConferenceRoomRequest (

        @NotBlank
        String name,

        @Min(2)
        int maxCapacity
) {
    public CreateConferenceRoomRequest(ConferenceRoom conferenceRoom) {
        this(
                conferenceRoom.getName(),
                conferenceRoom.getMaxCapacity()
        );
    }
}
