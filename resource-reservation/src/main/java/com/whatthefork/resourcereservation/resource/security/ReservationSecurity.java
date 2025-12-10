package com.whatthefork.resourcereservation.resource.security;

import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.entity.Reservation;
import com.whatthefork.resourcereservation.resource.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("reservationSecurity") // @PreAuthorize에서 참조할 이름 지정
@RequiredArgsConstructor
public class ReservationSecurity {

    private final ReservationRepository reservationRepository;

    public boolean isReservationOwner(String currentUserId, Long reservationId) {

        return reservationRepository.findById(reservationId)
                .map(Reservation::getUserId) // 예약 엔티티에서 소유자 ID를 가져옴
                .map(ownerId -> ownerId.equals(currentUserId)) // 현재 사용자 ID와 비교
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_ENOUGH_AUTHORITY)); // 예약이 존재하지 않으면 접근 불가
    }
}