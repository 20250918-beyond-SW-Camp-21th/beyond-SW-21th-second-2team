package com.whatthefork.resourcereservation.resource.entity;

import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateSuppliesRequest;
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
public class Supplies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean isBooked = false;

    @Builder
    public Supplies(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public Supplies updateName(String name) {
        this.name = name;
        return this;
    }

    public Supplies updateIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
        return this;
    }

    public Supplies updateAll(UpdateSuppliesRequest suppliesRequest) {
        this.name = suppliesRequest.name();
        this.capacity = suppliesRequest.capacity();
        this.isBooked = suppliesRequest.isBooked();

        return this;
    }
}
