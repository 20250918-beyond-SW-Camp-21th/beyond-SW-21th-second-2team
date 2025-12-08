package com.whatthefork.resourcereservation.resource.service;

import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateCorporateCarRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateCorporateCarRequest;
import com.whatthefork.resourcereservation.resource.dto.response.CorporateCarResponse;
import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import com.whatthefork.resourcereservation.resource.repository.CorporateCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorporateCarService {

    private final CorporateCarRepository corporateCarRepository;

    public List<CorporateCarResponse> getAllCorporateCars() {

        List<CorporateCar> CorporateCarEntityList = new ArrayList<>();

        CorporateCarEntityList = corporateCarRepository.findAll();

        return CorporateCarEntityList.stream()
                .map(resource -> {
                    return new CorporateCarResponse(resource);
                })
                .collect(Collectors.toList());
    }

    public CorporateCarResponse createCorporateCar(CreateCorporateCarRequest corporateCarRequest) {

        return new CorporateCarResponse(corporateCarRepository.save(
                CorporateCar.builder()
                        .name(corporateCarRequest.name())
                        .maxCapacity(corporateCarRequest.maxCapacity())
                        .carNumber(corporateCarRequest.carNumber())
                        .build()
        ));
    }

    public void deleteCorporateCar(Long id) {

        corporateCarRepository.deleteById(id);
    }

    public CorporateCarResponse updateCorporateCarById(Long id, UpdateCorporateCarRequest carRequest) {

        CorporateCar target = corporateCarRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        target.updateAll(carRequest);

        return new CorporateCarResponse(corporateCarRepository.save(target));
    }

    public CorporateCarResponse getCorporateCarById(Long id) {

        return new CorporateCarResponse(corporateCarRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    public CorporateCarResponse getCorporateCarByName(String name) {

        return new CorporateCarResponse(corporateCarRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
        );
    }

    public CorporateCarResponse getCorporateCarByMaxCapacity(int maxCapacity) {

        return new CorporateCarResponse(corporateCarRepository.findByMaxCapacity(maxCapacity));
    }
}
