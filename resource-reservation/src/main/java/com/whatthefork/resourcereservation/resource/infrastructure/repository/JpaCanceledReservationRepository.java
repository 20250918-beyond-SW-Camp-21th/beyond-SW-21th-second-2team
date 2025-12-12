package com.whatthefork.resourcereservation.resource.infrastructure.repository;

import com.whatthefork.resourcereservation.resource.entity.CanceledReservation;
import com.whatthefork.resourcereservation.resource.repository.CanceledReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCanceledReservationRepository extends CanceledReservationRepository, JpaRepository<CanceledReservation, Integer> {
}
