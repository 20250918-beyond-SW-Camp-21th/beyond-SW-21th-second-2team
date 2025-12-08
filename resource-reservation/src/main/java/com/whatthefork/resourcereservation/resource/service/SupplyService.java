package com.whatthefork.resourcereservation.resource.service;

import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateSuppliesRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateSuppliesRequest;
import com.whatthefork.resourcereservation.resource.dto.response.ConferenceRoomResponse;
import com.whatthefork.resourcereservation.resource.dto.response.SuppliesResponse;
import com.whatthefork.resourcereservation.resource.entity.Supplies;
import com.whatthefork.resourcereservation.resource.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;

    public List<SuppliesResponse> getAllSupplies() {

        List<Supplies> suppliesEntityList = new ArrayList<>();

        suppliesEntityList = supplyRepository.findAll();

        return suppliesEntityList.stream()
                .map(resource -> {return new SuppliesResponse(resource);})
                .collect(Collectors.toList());
    }

    public SuppliesResponse createSupply(CreateSuppliesRequest suppliesRequest) {

        return new SuppliesResponse(supplyRepository.save(
                Supplies.builder()
                        .name(suppliesRequest.name())
                        .capacity(suppliesRequest.capacity())
                        .build()
        ));
    }

    public void deleteSupply(Long id) {

        supplyRepository.deleteById(id);
    }

    public SuppliesResponse updateSupplyById(Long id, UpdateSuppliesRequest suppliesRequest) {

        Supplies target = supplyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        target.updateAll(suppliesRequest);

        return new SuppliesResponse(supplyRepository.save(target));
    }

    public SuppliesResponse getSupplyByName(String name) {

        return new SuppliesResponse(supplyRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }
}
