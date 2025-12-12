
package com.whatthefork.attendancetracking.annualLeave.service;

import com.whatthefork.attendancetracking.annualLeave.domain.AnnualLeave;
import com.whatthefork.attendancetracking.annualLeave.domain.AnnualLeaveHistory;
import com.whatthefork.attendancetracking.annualLeave.dto.LeaveAnnualRequestDto;
import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveHistoryResponse;
import com.whatthefork.attendancetracking.annualLeave.repository.AnnualLeaveHistoryRepository;
import com.whatthefork.attendancetracking.annualLeave.repository.AnnualLeaveRepository;
import com.whatthefork.attendancetracking.common.error.BusinessException;
import com.whatthefork.attendancetracking.common.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AnnualLeaveService - decreaseAnnual 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnnualLeaveService - 연차 차감 테스트")
class AnnualLeaveServiceTests {

    @Mock
    private AnnualLeaveRepository annualLeaveRepository;

    @Mock
    private AnnualLeaveHistoryRepository annualLeaveHistoryRepository;

    @InjectMocks
    private AnnualLeaveService annualLeaveService;

    private final Long MEMBER_ID = 100L;
    private final Long APPROVER_ID = 200L;

    // 1. 정상적으로 연차 차감 + 이력 저장 성공
    @Test
    @DisplayName("연차 차감 성공 시 usedLeave가 증가하고, 이력 테이블에 기록이 저장되어야 한다")
    void decreaseAnnual_success() {
        // Given
        LocalDate start = LocalDate.of(2025, 12, 11);
        LocalDate end   = LocalDate.of(2025, 12, 12);  // 2일 사용

        LeaveAnnualRequestDto requestDto = LeaveAnnualRequestDto.builder()
                .memberId(MEMBER_ID)
                .startDate(start)
                .endDate(end)
                .approverId(APPROVER_ID)
                .build();

        // 현재 연차: 총 10일, 이미 3일 사용 → 남은 7일
        AnnualLeave annualLeave = AnnualLeave.builder()
                .memberId(MEMBER_ID)
                .totalLeave(10)
                .usedLeave(3)
                .year(start.getYear())
                .build();

        given(annualLeaveRepository.findByMemberIdAndYear(MEMBER_ID, start.getYear()))
                .willReturn(annualLeave);

        // history 저장 mock
        given(annualLeaveHistoryRepository.save(any(AnnualLeaveHistory.class)))
                .willAnswer(invocation -> invocation.getArgument(0)); // 그대로 리턴

        // When
        AnnualLeaveHistoryResponse response = annualLeaveService.decreaseAnnual(requestDto);

        // Then
        // 1. AnnualLeave.usedLeave가 증가했는지
        assertEquals(5, annualLeave.getUsedLeave()); // 3 + 2

        // 2. history 저장이 한 번 호출되었는지
        verify(annualLeaveHistoryRepository, times(1))
                .save(any(AnnualLeaveHistory.class));

        // 3. 응답값 검증
        assertNotNull(response);
        assertEquals(MEMBER_ID, response.getMemberId());
        assertEquals(start, response.getStartDate());
        assertEquals(end, response.getEndDate());
        assertEquals(APPROVER_ID, response.getApproverId());
    }

    // 2. 해당 연도 연차 정보가 없는 경우
    @Test
    @DisplayName("해당 연도 연차 정보가 없으면 ANNUAL_LEAVE_NOT_FOUND 예외를 던져야 한다")
    void decreaseAnnual_annualLeaveNotFound_shouldThrowException() {
        // Given
        LocalDate start = LocalDate.of(2025, 12, 11);
        LocalDate end   = LocalDate.of(2025, 12, 11);

        LeaveAnnualRequestDto requestDto = LeaveAnnualRequestDto.builder()
                .memberId(MEMBER_ID)
                .startDate(start)
                .endDate(end)
                .approverId(APPROVER_ID)
                .build();

        given(annualLeaveRepository.findByMemberIdAndYear(MEMBER_ID, start.getYear()))
                .willReturn(null); // 또는 Optional 쓰면 Optional.empty()

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> annualLeaveService.decreaseAnnual(requestDto));

        assertEquals(ErrorCode.ANNUAL_LEAVE_NOT_FOUND, exception.getErrorCode());

        // history 저장 안 해야 함
        verify(annualLeaveHistoryRepository, never()).save(any());
    }

    // 3. 남은 연차가 부족한 경우
    @Test
    @DisplayName("남은 연차보다 더 많이 사용하려 하면 ANNUAL_LEAVE_INSUFFICIENT 예외를 던져야 한다")
    void decreaseAnnual_insufficientLeave_shouldThrowException() {
        // Given
        LocalDate start = LocalDate.of(2025, 12, 11);
        LocalDate end   = LocalDate.of(2025, 12, 15);  // 5일 사용 시도

        LeaveAnnualRequestDto requestDto = LeaveAnnualRequestDto.builder()
                .memberId(MEMBER_ID)
                .startDate(start)
                .endDate(end)
                .approverId(APPROVER_ID)
                .build();

        // 총 5일, 이미 3일 사용 → 남은 2일인데 5일 쓰려함
        AnnualLeave annualLeave = AnnualLeave.builder()
                .memberId(MEMBER_ID)
                .totalLeave(5)
                .usedLeave(3)
                .year(start.getYear())
                .build();

        given(annualLeaveRepository.findByMemberIdAndYear(MEMBER_ID, start.getYear()))
                .willReturn(annualLeave);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> annualLeaveService.decreaseAnnual(requestDto));

        assertEquals(ErrorCode.ANNUAL_LEAVE_INSUFFICIENT, exception.getErrorCode());

        // history 저장 안 해야 함
        verify(annualLeaveHistoryRepository, never()).save(any());
    }

    // 4. 날짜 범위가 잘못된 경우 (endDate < startDate)
    @Test
    @DisplayName("EndDate가 StartDate보다 빠르면 ANNUAL_LEAVE_INVALID_PERIOD 예외를 던져야 한다")
    void decreaseAnnual_invalidDateRange_shouldThrowException() {
        // Given
        LocalDate start = LocalDate.of(2025, 12, 15);
        LocalDate end   = LocalDate.of(2025, 12, 11); // 역전

        LeaveAnnualRequestDto requestDto = LeaveAnnualRequestDto.builder()
                .memberId(MEMBER_ID)
                .startDate(start)
                .endDate(end)
                .approverId(APPROVER_ID)
                .build();

        AnnualLeave annualLeave = AnnualLeave.builder()
                .memberId(MEMBER_ID)
                .totalLeave(10)
                .usedLeave(0)
                .year(start.getYear())
                .build();

        given(annualLeaveRepository.findByMemberIdAndYear(MEMBER_ID, start.getYear()))
                .willReturn(annualLeave);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> annualLeaveService.decreaseAnnual(requestDto));

        assertEquals(ErrorCode.ANNUAL_LEAVE_INVALID_PERIOD, exception.getErrorCode());

        // history 저장 안 해야 함
        verify(annualLeaveHistoryRepository, never()).save(any());
    }
}
