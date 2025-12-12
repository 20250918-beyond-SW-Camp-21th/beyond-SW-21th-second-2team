package com.whatthefork.resourcereservation.resource.repository;

import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import com.whatthefork.resourcereservation.resource.entity.Supplies;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplyRepository {

    Supplies save(Supplies resource);

    void deleteById(Long id);

    Optional<Supplies> findById(Long id);

    Optional<Supplies> findByName(String name);

    List<Supplies> findAll();
}
