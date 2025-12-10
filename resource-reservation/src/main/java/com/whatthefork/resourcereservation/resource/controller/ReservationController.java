package com.whatthefork.resourcereservation.resource.controller;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateReservationRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateReservationRequest;
import com.whatthefork.resourcereservation.resource.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Reservation", description = "회의실 API (추가, 수정, 삭제, 조회")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_AUTHORITY);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return null;
    }

    // 예약 목록 확인
    @Operation(summary = "예약 전체 조회", description = "존재하는 예약 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllReservations() {

        return ResponseEntity.ok(ApiResponse.success(reservationService.getAllReservations()));
    }

    // 예약 생성
    @Operation(summary = "예약 생성", description = "회의실, 법인차량, 비품 중 한 카테고리의 예약 생성")
    @PostMapping
    public ResponseEntity<ApiResponse> createReservation(@RequestBody CreateReservationRequest reservationRequest) {

        String userName = getUserId();
        Long userId = Long.parseLong(userName);

        switch(reservationRequest.category()) {
            case CONFERENCE_ROOM -> { return ResponseEntity.ok(ApiResponse.success(reservationService.createRoomReservation(reservationRequest, userId))); }
            case CORPORATE_VEHICLE -> { return ResponseEntity.ok(ApiResponse.success(reservationService.createVehicleReservation(reservationRequest, userId))); }
            case SUPPLIES -> { return ResponseEntity.ok(ApiResponse.success(reservationService.createSupplyReservation(reservationRequest, userId))); }
            default -> {return null;}
        }
    }

    // 예약 취소
    @Operation(summary = "예약 취소", description = "사용자의 예약 중 하나를 선택해 취소")
    @PostMapping("/cancellations/{id}")
    @PreAuthorize("hasRole('ADMIN') || reservationSecurity.isReservationOwner(principal.username, #id)")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable Long id) {

        String userName = getUserId();
        Long userId = Long.parseLong(userName);

        return ResponseEntity.ok(ApiResponse.success(reservationService.cancelReservation(id, userId)));
    }

    // 예약 수정
    @Operation(summary = "예약 수정", description = "사용자의 예약 중 사용 인원, 이용 기간을 수정")
    @PatchMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') || reservationSecurity.isReservationOwner(principal.username, #id)")
    public ResponseEntity<ApiResponse> editReservation(@PathVariable Long id, @RequestBody UpdateReservationRequest reservationRequest) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.editReservation(reservationRequest, id)));
    }

    // 내 만료 예약 목록 확인
    @Operation(summary = "만료 예약 조회", description = "사용자의 예약 중 만료된 항목을 조회")
    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMIN') || reservationSecurity.isReservationOwner(principal.username, #id)")
    public ResponseEntity<ApiResponse> expiredReservations() {

        String userName = getUserId();
        Long userId = Long.parseLong(userName);

        return ResponseEntity.ok(ApiResponse.success(reservationService.getExpiredReservations(userId)));
    }

    // 내 취소 예약 목록 확인
    @Operation(summary = "취소 예약 목록 확인", description = "사용자의 예약 중 취소된 항목을 조회")
    @GetMapping("/canceled")
    @PreAuthorize("hasRole('ADMIN') || reservationSecurity.isReservationOwner(principal.username, #id)")
    public ResponseEntity<ApiResponse> canceledReservations() {

        String userName = getUserId();
        Long userId = Long.parseLong(userName);

        return ResponseEntity.ok(ApiResponse.success(reservationService.getCanceledReservations(userId)));
    }
}
