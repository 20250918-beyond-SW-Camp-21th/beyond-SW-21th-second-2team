package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateSuppliesRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateSuppliesRequest;
import com.whatthefork.resourcereservation.resource.dto.response.SuppliesResponse;
import com.whatthefork.resourcereservation.resource.service.SupplyService;
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
@RequestMapping("/api/v1/supplies")
@RequiredArgsConstructor
public class SuppliesController {

    private final SupplyService supplyService;

    // 전체 비품 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getSupplies() {

        List<SuppliesResponse> suppliesResponsesList = supplyService.getAllSupplies();

        log.info("get all supplies: {} ", suppliesResponsesList);

        return ResponseEntity.ok(ApiResponse.success(suppliesResponsesList));
    }

    // 비품 추가
    @PostMapping
    public ResponseEntity<ApiResponse> createSupply(@RequestBody CreateSuppliesRequest suppliesRequest) {
        log.info("create supplies Request: {}", suppliesRequest);

        return ResponseEntity.ok(ApiResponse.success(supplyService.createSupply(suppliesRequest)));
    }

    // 비품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSupply(@PathVariable Long id) {
        log.info("delete supply: {}", id);

        supplyService.deleteSupply(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 비품 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSupply(@PathVariable Long id, @RequestBody UpdateSuppliesRequest suppliesRequest) {
        log.info("update supply: {}", id);

        return ResponseEntity.ok(ApiResponse.success(supplyService.updateSupplyById(id, suppliesRequest)));
    }

    // 비품 이름 검색
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getSupplyById(@PathVariable String name) {
        log.info("get supply by name: {}", name);

        return ResponseEntity.ok(ApiResponse.success(supplyService.getSupplyByName(name)));
    }

}