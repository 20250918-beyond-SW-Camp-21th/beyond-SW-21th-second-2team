package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateCorporateCarRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateCorporateCarRequest;
import com.whatthefork.resourcereservation.resource.dto.response.CorporateCarResponse;
import com.whatthefork.resourcereservation.resource.service.CorporateCarService;
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

@Tag(name = "Corporate Car", description = "법인차량 API (추가, 수정, 삭제, 조회")
@Slf4j
@RestController
@RequestMapping("/corporate-cars")
@RequiredArgsConstructor
public class CorporateCarController {

    private final CorporateCarService corporateCarService;

    // 법인차량 전체 목록 조회
    @Operation(summary = "법인차량 전체 조회", description = "존재하는 법인차량 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse> getCorporateCars() {

        List<CorporateCarResponse> corporateCarsList = corporateCarService.getAllCorporateCars();

        log.info("Get corporate cars : {} ",  corporateCarsList);

        return ResponseEntity.ok(ApiResponse.success(corporateCarsList));
    }

    // 법인차량 추가
    @Operation(summary = "법인차량 추가", description = "새로운 법인차량 추가")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createCorporateCar(@RequestBody CreateCorporateCarRequest corporateCarRequest) {
        log.info("create corporate car: {}", corporateCarRequest);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.createCorporateCar(corporateCarRequest)));
    }

    // 법인차량 삭제
    @Operation(summary = "법인차량 삭제", description = "존재하는 법인차량 삭제")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCorporateCar(@PathVariable Long id) {
        log.info("delete corporate car: {}", id);

        corporateCarService.deleteCorporateCar(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 법인차량 정보 수정
    @Operation(summary = "법인차량 수정", description = "법인차량에 대한 정보 수정")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCorporateCar(@PathVariable Long id, @RequestBody UpdateCorporateCarRequest corporateCarRequest) {
        log.info("update corporate car: {}", corporateCarRequest);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.updateCorporateCarById(id, corporateCarRequest)));
    }

    // 법인차량 이름으로 조회
    @Operation(summary = "법인차량 이름 검색", description = "이름으로 법인차량 검색")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCorporateCarByName(@PathVariable String name) {
        log.info("get corporate car by name : {}", name);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.getCorporateCarByName(name)));
    }

    // 법인차량 최대 인원으로 조회
    @Operation(summary = "법인차량 최대인원 검색", description = "법인차량 최대 인원으로 검색")
    @GetMapping("/maxCapacity/{maxCapacity}")
    public ResponseEntity<ApiResponse> getCorporateCarByMaxCapacity(@PathVariable int maxCapacity) {
        log.info("get corporate car by maxCapacity : {}", maxCapacity);

        return ResponseEntity.ok(ApiResponse.success(corporateCarService.getCorporateCarByMaxCapacity(maxCapacity)));
    }
}
