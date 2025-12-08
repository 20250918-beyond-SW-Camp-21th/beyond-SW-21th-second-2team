package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateCorporateCarRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateCorporateCarRequest;
import com.whatthefork.resourcereservation.resource.dto.response.CorporateCarResponse;
import com.whatthefork.resourcereservation.resource.service.CorporateCarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/corporate-cars")
@RequiredArgsConstructor
public class CorporateCarController {

    private final CorporateCarService corporateCarService;

    // 법인차량 전체 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getCorporateCars() {

        List<CorporateCarResponse> corporateCarsList = corporateCarService.getAllCorporateCars();

        log.info("Get corporate cars : {} ",  corporateCarsList);

        return ResponseEntity.ok(ApiResponse.success(corporateCarsList));
    }

    // 법인차량 추가
    @PostMapping
    public ResponseEntity<ApiResponse> createCorporateCar(@RequestBody CreateCorporateCarRequest corporateCarRequest) {
        log.info("create corporate car: {}", corporateCarRequest);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.createCorporateCar(corporateCarRequest)));
    }

    // 법인차량 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCorporateCar(@PathVariable Long id) {
        log.info("delete corporate car: {}", id);

        corporateCarService.deleteCorporateCar(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 법인차량 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCorporateCar(@PathVariable Long id, @RequestBody UpdateCorporateCarRequest corporateCarRequest) {
        log.info("update corporate car: {}", corporateCarRequest);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.updateCorporateCarById(id, corporateCarRequest)));
    }

    // 법인차량 이름으로 조회
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCorporateCarByName(@PathVariable String name) {
        log.info("get corporate car by name : {}", name);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.getCorporateCarByName(name)));
    }

    // 법인차량 최대 인원으로 조회
    @GetMapping("/maxCapacity/{maxCapacity}")
    public ResponseEntity<ApiResponse> getCorporateCarByMaxCapacity(@PathVariable int maxCapacity) {
        log.info("get corporate car by maxCapacity : {}", maxCapacity);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.getCorporateCarByMaxCapacity(maxCapacity)));
    }
}
