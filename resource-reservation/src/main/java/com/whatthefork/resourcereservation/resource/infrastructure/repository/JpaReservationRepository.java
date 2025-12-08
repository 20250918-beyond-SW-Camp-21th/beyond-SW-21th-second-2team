package com.whatthefork.resourcereservation.resource.infrastructure.repository;

import com.whatthefork.resourcereservation.resource.entity.Reservation;
import com.whatthefork.resourcereservation.resource.repository.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationRepository extends ReservationRepository, JpaRepository<Reservation, Long> {
}
