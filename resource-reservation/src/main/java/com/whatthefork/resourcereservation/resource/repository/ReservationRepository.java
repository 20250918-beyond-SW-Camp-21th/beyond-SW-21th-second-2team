package com.whatthefork.resourcereservation.resource.repository;

import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateReservationRequest;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationResponse;
import com.whatthefork.resourcereservation.resource.entity.Reservation;
import com.whatthefork.resourcereservation.resource.enums.ResourceCategory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository {

    List<Reservation> findAll();

    Reservation save(Reservation reservation);

    void deleteById(Long id);

    Optional<Reservation> findById(Long id);

    List<Reservation> findAllByUserId(Long userId);

    Optional<Reservation> findByUserId(Long userId);

    List<Reservation> findAllByUserIdAndCategory(Long userId, ResourceCategory category);
}
