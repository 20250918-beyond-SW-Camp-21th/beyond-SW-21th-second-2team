package com.whatthefork.resourcereservation.resource.entity;

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
public class CanceledReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private LocalDateTime canceledDate;

    @Builder
    public CanceledReservation(String reason, LocalDateTime canceledDate, Long userId) {
        this.reason = reason;
        this.canceledDate = canceledDate;
        this.userId = userId;
    }

    public CanceledReservation updateReason(String reason) {
        this.reason = reason;
        return this;
    }

    public CanceledReservation updateCanceledDate(LocalDateTime canceledDate) {
        this.canceledDate = canceledDate;
        return this;
    }
}
