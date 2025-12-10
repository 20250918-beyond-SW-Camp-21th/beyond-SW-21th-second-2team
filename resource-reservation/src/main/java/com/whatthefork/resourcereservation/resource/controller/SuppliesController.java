package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateSuppliesRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateSuppliesRequest;
import com.whatthefork.resourcereservation.resource.dto.response.SuppliesResponse;
import com.whatthefork.resourcereservation.resource.service.SupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Supplies", description = "비품 API (추가, 수정, 삭제, 조회")
@Slf4j
@RestController
@RequestMapping("/supplies")
@RequiredArgsConstructor
public class SuppliesController {

    private final SupplyService supplyService;

    // 전체 비품 조회
    @Operation(summary = "비품 전체 조회", description = "존재하는 비품 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse> getSupplies() {

        List<SuppliesResponse> suppliesResponsesList = supplyService.getAllSupplies();

        log.info("get all supplies: {} ", suppliesResponsesList);

        return ResponseEntity.ok(ApiResponse.success(suppliesResponsesList));
    }

    // 비품 추가
    @Operation(summary = "비품 추가", description = "새로운 비품 추가")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createSupply(@RequestBody CreateSuppliesRequest suppliesRequest) {
        log.info("create supplies Request: {}", suppliesRequest);

        return ResponseEntity.ok(ApiResponse.success(supplyService.createSupply(suppliesRequest)));
    }

    // 비품 삭제
    @Operation(summary = "비품 삭제", description = "존재하는 비품 삭제")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSupply(@PathVariable Long id) {
        log.info("delete supply: {}", id);

        supplyService.deleteSupply(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 비품 수정
    @Operation(summary = "비품 수정", description = "비품에 대한 정보 수정")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSupply(@PathVariable Long id, @RequestBody UpdateSuppliesRequest suppliesRequest) {
        log.info("update supply: {}", id);

        return ResponseEntity.ok(ApiResponse.success(supplyService.updateSupplyById(id, suppliesRequest)));
    }

    // 비품 이름 검색
    @Operation(summary = "비품 이름 검색", description = "이름으로 비품 검색")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getSupplyById(@PathVariable String name) {
        log.info("get supply by name: {}", name);

        return ResponseEntity.ok(ApiResponse.success(supplyService.getSupplyByName(name)));
    }

}