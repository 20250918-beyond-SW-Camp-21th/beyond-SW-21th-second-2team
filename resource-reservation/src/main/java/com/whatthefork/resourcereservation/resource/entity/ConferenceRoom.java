package com.whatthefork.resourcereservation.resource.entity;

import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateConferenceRoomRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConferenceRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private boolean isBooked = false;

    @Builder
    public ConferenceRoom(String name, int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public ConferenceRoom updateName(String name) {
        this.name = name;
        return this;
    }

    public ConferenceRoom updateMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public ConferenceRoom updateIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
        return this;
    }

    public ConferenceRoom updateAll(UpdateConferenceRoomRequest roomRequest) {
        this.name = roomRequest.name();
        this.maxCapacity = roomRequest.maxCapacity();
        this.isBooked = roomRequest.isBooked();

        return this;
    }
}
