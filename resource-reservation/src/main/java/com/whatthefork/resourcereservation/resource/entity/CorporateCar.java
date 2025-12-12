package com.whatthefork.resourcereservation.resource.entity;

import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateCorporateCarRequest;
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
public class CorporateCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String carNumber;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private boolean isBooked = false;

    @Builder
    public CorporateCar(String name, int maxCapacity, String carNumber) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.carNumber = carNumber;
    }

    public CorporateCar updateName(String name) {
        this.name = name;
        return this;
    }

    public CorporateCar updateMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public CorporateCar updateIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
        return this;
    }

    public CorporateCar updateAll(UpdateCorporateCarRequest carRequest) {
        this.name = carRequest.name();
        this.maxCapacity = carRequest.maxCapacity();
        this.isBooked = carRequest.isBooked();
        this.carNumber = carRequest.carNumber();

        return this;
    }
}
