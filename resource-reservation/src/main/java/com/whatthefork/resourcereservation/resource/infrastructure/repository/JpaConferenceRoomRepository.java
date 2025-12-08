package com.whatthefork.resourcereservation.resource.infrastructure.repository;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import com.whatthefork.resourcereservation.resource.repository.ConferenceRoomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConferenceRoomRepository extends ConferenceRoomRepository, JpaRepository<ConferenceRoom, Long> {
}
