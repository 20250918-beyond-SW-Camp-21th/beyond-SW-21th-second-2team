package com.whatthefork.resourcereservation.resource.entity;

import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateReservationRequest;
import com.whatthefork.resourcereservation.resource.enums.ResourceCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;                // user table의 pk 참조하는 fk

    @Column(nullable = false)
    private Long resourceId;            // resource table의 pk 참조하는 fk

    @Column(nullable = false)
    private LocalDateTime bookedDate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private ResourceCategory category;

    @Column(nullable = false)
    private boolean isExpired = false;

    @Builder
    public Reservation(Long userId, Long resourceId, LocalDateTime bookedDate, LocalDateTime startDate, LocalDateTime endDate, int capacity, String reason, ResourceCategory category) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.bookedDate = bookedDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.capacity = capacity;
        this.reason = reason;
        this.category = category;
    }

    public Reservation updateBookedDate(LocalDateTime bookedDate) {
        this.bookedDate = bookedDate;
        return this;
    }

    public Reservation updateStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public Reservation updateEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public Reservation updateReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Reservation updateCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public Reservation updateIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
        return this;
    }

    public Reservation updateAll(UpdateReservationRequest reservation) {
        this.resourceId = reservation.resourceId();
        this.bookedDate = reservation.bookedDate();
        this.startDate = reservation.startDate();
        this.endDate = reservation.endDate();
        this.capacity = reservation.capacity();
        this.reason = reservation.reason();
        this.category = reservation.category();

        return this;
    }
}
