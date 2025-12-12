package com.whatthefork.resourcereservation.resource.infrastructure.repository;

import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import com.whatthefork.resourcereservation.resource.repository.CorporateCarRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCorporateCarRepository extends CorporateCarRepository, JpaRepository<CorporateCar, Long> {
}
