package com.whatthefork.resourcereservation.resource.dto.response;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConferenceRoomResponse(

        @NotNull
        Long id,

        @NotBlank
        String name,

        @Min(2)
        int maxCapacity,

        @NotNull
        boolean isBooked
) {
    public ConferenceRoomResponse(ConferenceRoom conferenceRoom) {
        this(
                conferenceRoom.getId(),
                conferenceRoom.getName(),
                conferenceRoom.getMaxCapacity(),
                conferenceRoom.isBooked()
        );
    }
}