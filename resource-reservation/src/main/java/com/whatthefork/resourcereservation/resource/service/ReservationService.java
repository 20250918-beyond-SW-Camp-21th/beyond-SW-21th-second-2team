package com.whatthefork.resourcereservation.resource.service;

import com.whatthefork.resourcereservation.exception.BusinessException;
import com.whatthefork.resourcereservation.exception.ErrorCode;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateReservationRequest;
import com.whatthefork.resourcereservation.resource.dto.request.update.UpdateReservationRequest;
import com.whatthefork.resourcereservation.resource.dto.response.CanceledReservationResponse;
import com.whatthefork.resourcereservation.resource.dto.response.ConferenceRoomResponse;
import com.whatthefork.resourcereservation.resource.dto.response.CorporateCarResponse;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationAndConferenceRoom;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationAndCorporateCar;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationAndSupply;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationResponse;
import com.whatthefork.resourcereservation.resource.dto.response.SuppliesResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConferenceRoomRepository conferenceRoomRepository;
    private final CorporateCarRepository corporateCarRepository;
    private final SupplyRepository supplyRepository;
    private final CanceledReservationRepository cancelRepository;

    public List<ReservationResponse> getAllReservations() {

        return reservationRepository.findAll().stream()
                .map(reservation -> { return new ReservationResponse(reservation); })
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationAndConferenceRoom createRoomReservation(CreateReservationRequest reservationRequest, Long userId) {

        ReservationResponse response = getReservationResponse(reservationRequest, userId);

        ConferenceRoom conferenceRoom = conferenceRoomRepository.findById(reservationRequest.resourceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (conferenceRoom.isBooked()) {
            throw new BusinessException(ErrorCode.REDUNDANT_RESERVATION);
        }

        conferenceRoom.updateIsBooked(true);
        conferenceRoomRepository.save(conferenceRoom);

        ConferenceRoomResponse conferenceRoomResponse = new ConferenceRoomResponse(conferenceRoomRepository
                .findById(reservationRequest.resourceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)));

        ReservationAndConferenceRoom reservationAndConferenceRoom =
                new ReservationAndConferenceRoom(response, conferenceRoomResponse);

        return reservationAndConferenceRoom;
    }

    @Transactional
    public ReservationAndCorporateCar createVehicleReservation(CreateReservationRequest reservationRequest, Long userId) {

        ReservationResponse response = getReservationResponse(reservationRequest, userId);

        CorporateCar corporateCar = corporateCarRepository.findById(reservationRequest.resourceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (corporateCar.isBooked()) {
            throw new BusinessException(ErrorCode.REDUNDANT_RESERVATION);
        }

        corporateCar.updateIsBooked(true);
        corporateCarRepository.save(corporateCar);

        CorporateCarResponse corporateCarResponse = new CorporateCarResponse(corporateCarRepository
                .findById(reservationRequest.resourceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)));

        ReservationAndCorporateCar reservationAndCorporateCar =
                new ReservationAndCorporateCar(response, corporateCarResponse);

        return reservationAndCorporateCar;
    }

    @Transactional
    public ReservationAndSupply createSupplyReservation(CreateReservationRequest reservationRequest, Long userId) {

        ReservationResponse response = getReservationResponse(reservationRequest, userId);

        Supplies supply = supplyRepository.findById(reservationRequest.resourceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (supply.isBooked()) {
            throw new BusinessException(ErrorCode.NOT_ENOUGH_CAPACITY);
        } else {
            supply.updateCapacity(supply.getCapacity() - 1);

            SuppliesResponse suppliesResponse = new SuppliesResponse(supplyRepository
                    .findById(reservationRequest.resourceId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)));

            ReservationAndSupply reservationAndSupply =
                    new ReservationAndSupply(response, suppliesResponse);

            if (supply.getCapacity() == 0) supply.updateIsBooked(true);

            return reservationAndSupply;
        }
    }

    @Transactional
    public CanceledReservationResponse cancelReservation(Long id, Long userId) {

        // 취소할 예약 가져오기
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // 예약 취소 테이블에 데이터 추가
        CanceledReservation canceledReservation = cancelRepository.save(CanceledReservation.builder()
                        .reason(reservation.getReason())
                        .canceledDate(LocalDateTime.now())
                        .userId(userId)
                        .build()
                );

        // 잡혀 있던 자원 상태 예약 가능으로 바꾸기
        switch (reservation.getCategory()) {
            case CONFERENCE_ROOM -> {
                conferenceRoomRepository.findById(reservation.getResourceId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
                        .updateIsBooked(false);
            }
            case CORPORATE_VEHICLE -> {
                corporateCarRepository.findById(reservation.getResourceId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
                        .updateIsBooked(false);
            }
            case SUPPLIES -> {
                supplyRepository.findById(reservation.getResourceId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
                        .updateIsBooked(false);
            }
        }

        // 있던 예약 삭제
        reservationRepository.deleteById(id);

        return new CanceledReservationResponse(canceledReservation);
    }

    // 예약 수정
    @Transactional
    public ReservationResponse editReservation(UpdateReservationRequest request, Long id) {

        return new ReservationResponse(reservationRepository.save(
                reservationRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
                        .updateAll(request)
        ));
    }

    // 만료 예약 확인
    @Transactional
    public List<ReservationResponse> getExpiredReservations(Long userId) {
        log.info("get in");

        List<Reservation> allReservations = reservationRepository.findAllByUserId(userId);

        List<Reservation> expiredReservations = allReservations.stream()
                .filter(reservation -> {
                    if (reservation.getEndDate().isBefore(LocalDateTime.now())) {
                        reservation.updateIsExpired(true);
                        return true;
                    }
                    return false;
                })
                .toList();

        return expiredReservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<CanceledReservationResponse> getCanceledReservations(Long userId) {

        return cancelRepository.findAllByUserId(userId).stream()
                .map(CanceledReservationResponse::new)
                .toList();
    }

    public ReservationResponse getReservationResponse(CreateReservationRequest reservationRequest, Long userId) {

        return new ReservationResponse(reservationRepository.save(
                Reservation.builder()
                        .userId(userId)
                        .resourceId(reservationRequest.resourceId())
                        .bookedDate(reservationRequest.bookedDate())
                        .startDate(reservationRequest.startDate())
                        .endDate(reservationRequest.endDate())
                        .capacity(reservationRequest.capacity())
                        .reason(reservationRequest.reason())
                        .category(reservationRequest.category())
                        .build()
        ));
    }

    public List<ReservationResponse> getMyReservationsByCategory(Long userId, ResourceCategory category) {

        return reservationRepository.findAllByUserIdAndCategory(userId, category).stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> getMyReservations(Long userId) {

        return reservationRepository.findAllByUserId(userId).stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
    }
}
