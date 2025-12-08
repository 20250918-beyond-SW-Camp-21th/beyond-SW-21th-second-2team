package com.whatthefork.resourcereservation.resource.infrastructure.repository;

import com.whatthefork.resourcereservation.resource.entity.Supplies;
import com.whatthefork.resourcereservation.resource.repository.SupplyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSupplyRepository extends SupplyRepository, JpaRepository<Supplies, Long> {
}
