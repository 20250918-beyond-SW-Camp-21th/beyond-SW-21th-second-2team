package com.whatthefork.resourcereservation.resource.repository;

import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorporateCarRepository {

    CorporateCar save(CorporateCar resource);

    void deleteById(Long id);

    Optional<CorporateCar> findById(Long id);

    Optional<CorporateCar> findByName(String name);

    CorporateCar findByMaxCapacity(int maxCapacity);

    List<CorporateCar> findAll();
}
