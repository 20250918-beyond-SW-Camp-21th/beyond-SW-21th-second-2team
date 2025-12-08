package com.whatthefork.resourcereservation.resource.repository;

import com.whatthefork.resourcereservation.resource.dto.response.CanceledReservationResponse;
import com.whatthefork.resourcereservation.resource.entity.CanceledReservation;
import com.whatthefork.resourcereservation.resource.entity.Reservation;

import java.util.List;

public interface CanceledReservationRepository {
    CanceledReservation save(CanceledReservation reservation);

    List<CanceledReservation> findAllByUserId(Long userId);
}
