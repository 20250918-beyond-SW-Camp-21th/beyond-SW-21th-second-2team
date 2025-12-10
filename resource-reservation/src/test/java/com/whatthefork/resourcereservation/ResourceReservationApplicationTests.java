package com.whatthefork.resourcereservation;

import com.whatthefork.resourcereservation.common.ApiResponse;
import com.whatthefork.resourcereservation.resource.controller.ReservationController;
import com.whatthefork.resourcereservation.resource.dto.request.create.CreateReservationRequest;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationAndConferenceRoom;
import com.whatthefork.resourcereservation.resource.dto.response.ReservationAndCorporateCar; // New Import
import com.whatthefork.resourcereservation.resource.dto.response.ReservationAndSupply; // New Import
import com.whatthefork.resourcereservation.resource.dto.response.ReservationResponse;
import com.whatthefork.resourcereservation.resource.dto.response.ConferenceRoomResponse;
import com.whatthefork.resourcereservation.resource.dto.response.CorporateCarResponse; // New Import
import com.whatthefork.resourcereservation.resource.dto.response.SuppliesResponse; // New Import
import com.whatthefork.resourcereservation.resource.service.ReservationService;
import com.whatthefork.resourcereservation.resource.enums.ResourceCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ReservationController 단위 테스트 (CreateReservation)")
class ReservationControllerUnitTest {

    // Mockito의 @Mock을 사용하여 의존성 객체(Service)를 가짜 객체로 대체합니다.
    @Mock
    private ReservationService reservationService;

    // Mock 객체들을 실제 테스트 대상인 Controller에 주입합니다. (DI 역할)
    @InjectMocks
    private ReservationController reservationController;

    private AutoCloseable closeable;

    // 테스트에 사용되는 상수 (인증/시간 등) 정의
    private final String MOCK_USER_NAME = "20251210";
    private final Long MOCK_USER_ID = 20251210L;
    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDateTime FUTURE_START = NOW.plusDays(1);
    private final LocalDateTime FUTURE_END = NOW.plusDays(2);


    @BeforeEach
    void setUp() {
        // 모든 @Mock, @InjectMocks 애노테이션이 붙은 필드를 초기화합니다.
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        // 테스트 실행 후 Security Context를 반드시 비워 다음 테스트에 영향을 주지 않도록 합니다.
        SecurityContextHolder.clearContext();
        closeable.close();
    }

    /**
     * 컨트롤러 내부의 getUserId() 호출을 시뮬레이션하기 위해
     * Spring Security Context에 인증된 사용자 정보를 수동으로 설정하는 헬퍼 메서드입니다.
     */
    private void setAuthenticatedUser(String username) {
        // UserDetails (사용자 정보) 생성. username 필드에 사용자 ID를 넣습니다.
        UserDetails userDetails = new User(username, "dummy",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Security Context에 인증 정보 주입
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * 테스트에 사용할 CreateReservationRequest DTO를 생성합니다.
     */
    private CreateReservationRequest createMockRequest(ResourceCategory category) {
        // DTO 필드 중 Future 제약 조건을 만족하도록 날짜를 설정합니다.
        return new CreateReservationRequest(
                1L, // resourceId
                NOW.minusHours(1), // bookedDate (Past를 만족시키기 위해)
                FUTURE_START, // startDate
                FUTURE_END, // endDate
                2, // capacity
                "테스트 사유", // reason
                category
        );
    }

    // --- 1. 회의실 예약 테스트 (ReservationAndConferenceRoom 반환) ---

    @Test
    @DisplayName("회의실 예약 시 createRoomReservation이 호출되고 ReservationAndConferenceRoom을 반환해야 한다")
    void createReservation_ConferenceRoom_ShouldCallRoomService() {
        // Given
        setAuthenticatedUser(MOCK_USER_NAME); // 인증된 사용자 설정

        CreateReservationRequest request = createMockRequest(ResourceCategory.CONFERENCE_ROOM);

        // Service 메서드가 반환할 Mock DTO를 생성합니다.
        ReservationAndConferenceRoom expectedResponseDto = new ReservationAndConferenceRoom(
                mock(ReservationResponse.class),
                mock(ConferenceRoomResponse.class)
        );

        // Stubbing: Service 메서드 호출 시 예상 응답 DTO를 반환하도록 설정합니다.
        given(reservationService.createRoomReservation(any(CreateReservationRequest.class), eq(MOCK_USER_ID)))
                .willReturn(expectedResponseDto);

        // When
        // 컨트롤러 메서드를 직접 호출합니다. (MockMvc 미사용)
        ResponseEntity<ApiResponse> response = reservationController.createReservation(request);

        // Then
        // 1. HTTP 상태 코드와 응답 데이터 검증
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponseDto, response.getBody().getData());

        // 2. Service 메서드 호출 검증 (핵심)
        // createRoomReservation이 1회, 정확한 userId와 request로 호출되었는지 확인
        verify(reservationService, times(1)).createRoomReservation(any(CreateReservationRequest.class), eq(MOCK_USER_ID));
        // 다른 카테고리 Service 메서드는 호출되지 않았는지 확인 (분기 로직 성공 검증)
        verify(reservationService, never()).createVehicleReservation(any(), any());
        verify(reservationService, never()).createSupplyReservation(any(), any());
    }

    // --- 2. 법인차량 예약 테스트 (ReservationAndCorporateCar 반환) ---

    @Test
    @DisplayName("법인차량 예약 시 createVehicleReservation이 호출되고 ReservationAndCorporateCar를 반환해야 한다")
    void createReservation_CorporateVehicle_ShouldCallVehicleService() {
        // Given
        setAuthenticatedUser(MOCK_USER_NAME);
        CreateReservationRequest request = createMockRequest(ResourceCategory.CORPORATE_VEHICLE);

        // Stubbing을 위한 예상 응답 DTO 생성
        ReservationAndCorporateCar expectedResponseDto = new ReservationAndCorporateCar(
                mock(ReservationResponse.class),
                mock(CorporateCarResponse.class)
        );

        given(reservationService.createVehicleReservation(any(CreateReservationRequest.class), eq(MOCK_USER_ID)))
                .willReturn(expectedResponseDto);

        // When
        ResponseEntity<ApiResponse> response = reservationController.createReservation(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponseDto, response.getBody().getData());

        // Service 호출 검증
        verify(reservationService, times(1)).createVehicleReservation(any(CreateReservationRequest.class), eq(MOCK_USER_ID));
        verify(reservationService, never()).createRoomReservation(any(), any());
        verify(reservationService, never()).createSupplyReservation(any(), any());
    }

    // --- 3. 비품 예약 테스트 (ReservationAndSupply 반환) ---

    @Test
    @DisplayName("비품 예약 시 createSupplyReservation이 호출되고 ReservationAndSupply를 반환해야 한다")
    void createReservation_Supplies_ShouldCallSupplyService() {
        // Given
        setAuthenticatedUser(MOCK_USER_NAME);
        CreateReservationRequest request = createMockRequest(ResourceCategory.SUPPLIES);

        // Stubbing을 위한 예상 응답 DTO 생성
        ReservationAndSupply expectedResponseDto = new ReservationAndSupply(
                mock(ReservationResponse.class),
                mock(SuppliesResponse.class)
        );

        given(reservationService.createSupplyReservation(any(CreateReservationRequest.class), eq(MOCK_USER_ID)))
                .willReturn(expectedResponseDto);

        // When
        ResponseEntity<ApiResponse> response = reservationController.createReservation(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponseDto, response.getBody().getData());

        // Service 호출 검증
        verify(reservationService, times(1)).createSupplyReservation(any(CreateReservationRequest.class), eq(MOCK_USER_ID));
        verify(reservationService, never()).createRoomReservation(any(), any());
        verify(reservationService, never()).createVehicleReservation(any(), any());
    }
}