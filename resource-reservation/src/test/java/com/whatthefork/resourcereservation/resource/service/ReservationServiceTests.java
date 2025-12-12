package com.whatthefork.resourcereservation.resource.service;

import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.dto.response.CanceledReservationResponse;
import com.whatthefork.resourcereservation.resource.entity.CanceledReservation;
import com.whatthefork.resourcereservation.resource.entity.ConferenceRoom;
import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import com.whatthefork.resourcereservation.resource.entity.Reservation;
import com.whatthefork.resourcereservation.resource.entity.Supplies;
import com.whatthefork.resourcereservation.resource.enums.ResourceCategory;
import com.whatthefork.resourcereservation.resource.repository.CanceledReservationRepository;
import com.whatthefork.resourcereservation.resource.repository.ConferenceRoomRepository;
import com.whatthefork.resourcereservation.resource.repository.CorporateCarRepository;
import com.whatthefork.resourcereservation.resource.repository.ReservationRepository;
import com.whatthefork.resourcereservation.resource.repository.SupplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService - 예약 취소 테스트")
class ReservationServiceTest {

    // Repository는 Mocking 대상
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ConferenceRoomRepository conferenceRoomRepository;
    @Mock
    private CorporateCarRepository corporateCarRepository;
    @Mock
    private SupplyRepository supplyRepository;
    @Mock
    private CanceledReservationRepository cancelRepository;

    // 테스트 대상 Service에 Mock들을 주입
    @InjectMocks
    private ReservationService reservationService;

    // 테스트에 사용할 공통 변수
    private final Long RESERVATION_ID = 1L;
    private final Long USER_ID = 100L;
    private final Long RESOURCE_ID = 200L;
    private final String REASON = "일정 변경";


    // 1. 예약이 존재하지 않는 경우
    @Test
    @DisplayName("예약 ID가 존재하지 않으면 RESOURCE_NOT_FOUND 예외를 던져야 한다")
    void cancelReservation_ReservationNotFound_ShouldThrowException() {
        // Given
        given(reservationRepository.findById(RESERVATION_ID)).willReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.cancelReservation(RESERVATION_ID, USER_ID);
        });

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verify(cancelRepository, never()).save(any(CanceledReservation.class));
        verify(reservationRepository, never()).deleteById(any());
    }


    // 2. 회의실 예약 취소 성공
    @Test
    @DisplayName("회의실 예약 취소 시, 회의실 상태를 false로 업데이트하고 예약을 삭제해야 한다")
    void cancelReservation_ConferenceRoom_Success() {
        // Given
        // 1. 예약 객체 Mocking
        Reservation mockReservation = mock(Reservation.class);
        given(mockReservation.getCategory()).willReturn(ResourceCategory.CONFERENCE_ROOM);
        given(mockReservation.getResourceId()).willReturn(RESOURCE_ID);
        given(mockReservation.getReason()).willReturn(REASON);
        given(reservationRepository.findById(RESERVATION_ID)).willReturn(Optional.of(mockReservation));

        // 2. 자원 객체 Mocking (updateIsBooked 메서드 호출 검증을 위해 필요)
        ConferenceRoom mockRoom = mock(ConferenceRoom.class);
        given(conferenceRoomRepository.findById(RESOURCE_ID)).willReturn(Optional.of(mockRoom));

        // 3. 취소 기록 저장 Mocking
        CanceledReservation mockCanceled = CanceledReservation.builder().userId(50L).build();
        given(cancelRepository.save(any(CanceledReservation.class))).willReturn(mockCanceled);

        // When
        CanceledReservationResponse response = reservationService.cancelReservation(RESERVATION_ID, USER_ID);

        // Then
        // 1. 취소 기록 저장 검증
        verify(cancelRepository, times(1)).save(any(CanceledReservation.class));
        // 2. 자원 상태 업데이트 검증
        verify(mockRoom, times(1)).updateIsBooked(eq(false));
        // 3. 예약 삭제 검증
        verify(reservationRepository, times(1)).deleteById(RESERVATION_ID);
        // 4. 응답 확인
        assertNotNull(response);
    }


    // 3. 법인차량 예약 취소 성공
    @Test
    @DisplayName("법인차량 예약 취소 시, 차량 상태를 false로 업데이트하고 예약을 삭제해야 한다")
    void cancelReservation_CorporateCar_Success() {
        // Given
        // 1. 예약 객체 Mocking
        Reservation mockReservation = mock(Reservation.class);
        given(mockReservation.getCategory()).willReturn(ResourceCategory.CORPORATE_VEHICLE);
        given(mockReservation.getResourceId()).willReturn(RESOURCE_ID);
        given(mockReservation.getReason()).willReturn(REASON);
        given(reservationRepository.findById(RESERVATION_ID)).willReturn(Optional.of(mockReservation));

        // 2. 자원 객체 Mocking
        CorporateCar mockCar = mock(CorporateCar.class);
        given(corporateCarRepository.findById(RESOURCE_ID)).willReturn(Optional.of(mockCar));

        // 3. 취소 기록 저장 Mocking
        CanceledReservation mockCanceled = CanceledReservation.builder().userId(51L).build();
        given(cancelRepository.save(any(CanceledReservation.class))).willReturn(mockCanceled);

        // When
        reservationService.cancelReservation(RESERVATION_ID, USER_ID);

        // Then
        // 1. 취소 기록 저장 검증
        verify(cancelRepository, times(1)).save(any(CanceledReservation.class));
        // 2. 자원 상태 업데이트 검증
        verify(mockCar, times(1)).updateIsBooked(eq(false));
        // 3. 예약 삭제 검증
        verify(reservationRepository, times(1)).deleteById(RESERVATION_ID);
        // (ConferenceRoom, Supplies Repository는 호출되지 않았는지 확인도 가능)
    }

    // 4. 비품 예약 취소 성공
    @Test
    @DisplayName("비품 예약 취소 시, 비품 상태를 false로 업데이트하고 예약을 삭제해야 한다")
    void cancelReservation_Supplies_Success() {
        // Given
        // 1. 예약 객체 Mocking
        Reservation mockReservation = mock(Reservation.class);
        given(mockReservation.getCategory()).willReturn(ResourceCategory.SUPPLIES);
        given(mockReservation.getResourceId()).willReturn(RESOURCE_ID);
        given(mockReservation.getReason()).willReturn(REASON);
        given(reservationRepository.findById(RESERVATION_ID)).willReturn(Optional.of(mockReservation));

        // 2. 자원 객체 Mocking
        Supplies mockSupply = mock(Supplies.class);
        given(supplyRepository.findById(RESOURCE_ID)).willReturn(Optional.of(mockSupply));

        // 3. 취소 기록 저장 Mocking
        CanceledReservation mockCanceled = CanceledReservation.builder().userId(52L).build();
        given(cancelRepository.save(any(CanceledReservation.class))).willReturn(mockCanceled);

        // When
        reservationService.cancelReservation(RESERVATION_ID, USER_ID);

        // Then
        // 1. 취소 기록 저장 검증
        verify(cancelRepository, times(1)).save(any(CanceledReservation.class));
        // 2. 자원 상태 업데이트 검증
        verify(mockSupply, times(1)).updateIsBooked(eq(false));
        // 3. 예약 삭제 검증
        verify(reservationRepository, times(1)).deleteById(RESERVATION_ID);
    }

    // 5. 예약은 있으나 자원이 DB에 없는 경우
    @Test
    @DisplayName("예약된 자원(ConferenceRoom)이 DB에 없으면 RESOURCE_NOT_FOUND 예외를 던져야 한다")
    void cancelReservation_ResourceMissing_ShouldThrowException() {
        // Given
        // 1. 예약 객체 Mocking: 회의실 예약이었으나
        Reservation mockReservation = mock(Reservation.class);
        given(mockReservation.getCategory()).willReturn(ResourceCategory.CONFERENCE_ROOM);
        given(mockReservation.getResourceId()).willReturn(RESOURCE_ID);
        given(mockReservation.getReason()).willReturn(REASON);
        given(reservationRepository.findById(RESERVATION_ID)).willReturn(Optional.of(mockReservation));

        // 2. 해당 회의실이 Repository에 없는 경우 (Optional.empty())
        given(conferenceRoomRepository.findById(RESOURCE_ID)).willReturn(Optional.empty());

        // 3. 취소 기록 저장 Mocking (예외 발생 직전에 save는 호출될 수 있음)
        given(cancelRepository.save(any(CanceledReservation.class))).willReturn(mock(CanceledReservation.class));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.cancelReservation(RESERVATION_ID, USER_ID);
        });

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        // 자원 업데이트가 실패했으므로 예약 삭제는 호출되지 않아야 함 (트랜잭션 롤백 기대)
        verify(reservationRepository, never()).deleteById(any());
    }
}