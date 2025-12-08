package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateReservationRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateReservationRequest;
import com.whatthefork.resourcereservation.resource.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final Long userId = 3L;   // 나중에 토큰에서 userId 뽑아와야 함

    // 예약 목록 확인
    @GetMapping
    public ResponseEntity<ApiResponse> getAllReservations() {

        return ResponseEntity.ok(ApiResponse.success(reservationService.getAllReservations()));
    }

    // 예약 생성
    @PostMapping
    public ResponseEntity<ApiResponse> createReservation(@RequestBody CreateReservationRequest reservationRequest) {

        switch(reservationRequest.category()) {
            case CONFERENCE_ROOM -> { return ResponseEntity.ok(ApiResponse.success(reservationService.createRoomReservation(reservationRequest, userId))); }
            case CORPORATE_VEHICLE -> { return ResponseEntity.ok(ApiResponse.success(reservationService.createVehicleReservation(reservationRequest, userId))); }
            case SUPPLIES -> { return ResponseEntity.ok(ApiResponse.success(reservationService.createSupplyReservation(reservationRequest, userId))); }
            default -> {return null;}
        }
    }

    // 예약 취소
    @PostMapping("/cancellations/{id}")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.cancelReservation(id, userId)));
    }

    // 예약 수정
    @PatchMapping("/edit/{id}")
    public ResponseEntity<ApiResponse> editReservation(@PathVariable Long id, @RequestBody UpdateReservationRequest reservationRequest) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.editReservation(reservationRequest, id)));
    }

    // 내 만료 예약 목록 확인
    @GetMapping("/expired")
    public ResponseEntity<ApiResponse> expiredReservations() {

        return ResponseEntity.ok(ApiResponse.success(reservationService.getExpiredReservations(userId)));
    }

    // 내 취소 예약 목록 확인
    @GetMapping("/canceled")
    public ResponseEntity<ApiResponse> canceledReservations() {

        return ResponseEntity.ok(ApiResponse.success(reservationService.getCanceledReservations(userId)));
    }
}
