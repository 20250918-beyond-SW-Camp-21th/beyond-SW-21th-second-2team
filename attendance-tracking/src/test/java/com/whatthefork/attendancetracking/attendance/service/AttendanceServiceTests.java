package com.whatthefork.attendancetracking.attendance.service;

import static org.junit.jupiter.api.Assertions.*;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import com.whatthefork.attendancetracking.attendance.repository.AttendanceRepository;
import com.whatthefork.attendancetracking.common.error.BusinessException;
import com.whatthefork.attendancetracking.common.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService - 출퇴근 테스트")
class AttendanceServiceTests {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private static final Long USER_ID = 100L;

    // 1. 정상 출근
    @Test
    @DisplayName("오늘 처음 출근하면 Attendance가 저장되어야 한다")
    void checkIn_success() {
        // given
        // 오늘 출근한 기록 없음
        given(attendanceRepository.findAttendanceByUserId(
                eq(USER_ID), any(LocalDateTime.class), any(LocalDateTime.class))
        ).willReturn(Optional.empty());

        // when
        attendanceService.checkIn(USER_ID);

        // then
        // 저장된 Attendance 캡쳐
        ArgumentCaptor<Attendance> captor = ArgumentCaptor.forClass(Attendance.class);
        verify(attendanceRepository, times(1)).save(captor.capture());

        Attendance saved = captor.getValue();
        assertEquals(USER_ID, saved.getUserId());
        assertNotNull(saved.getPunchInDate());
        assertNull(saved.getPunchOutDate());
        assertEquals(0, saved.getOvertimeMinutes());
        // isLate,lateMinutes 는 LocalTime.now()에 따라 달라져서 여기서는 구체값까지는 검증 X
    }

    // 2. 이미 오늘 출근한 상태에서 또 출근 찍으면 예외
    @Test
    @DisplayName("오늘 이미 출근한 사용자가 다시 출근 체크하면 ATTENDANCE_ALREADY_CHECKED_IN 예외가 발생해야 한다")
    void checkIn_alreadyCheckedIn_shouldThrowException() {
        // given
        Attendance mockAttendance = mock(Attendance.class);
        given(attendanceRepository.findAttendanceByUserId(
                eq(USER_ID), any(LocalDateTime.class), any(LocalDateTime.class))
        ).willReturn(Optional.of(mockAttendance));

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> attendanceService.checkIn(USER_ID));

        assertEquals(ErrorCode.ATTENDANCE_ALREADY_CHECKED_IN, ex.getErrorCode());
        // 이미 출근 상태이므로 save는 호출되면 안 됨
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    // 3. 정상 퇴근
    @Test
    @DisplayName("퇴근 시, 마지막으로 출근했지만 아직 퇴근하지 않은 기록에 퇴근 시간이 저장되어야 한다")
    void checkOut_success() {
        // given
        // 아직 퇴근 안 찍힌 출근 기록
        LocalDateTime punchIn = LocalDateTime.now().minusHours(9);
        Attendance attendance = Attendance.builder()
                .userId(USER_ID)
                .punchInDate(punchIn)
                .punchOutDate(null)
                .isLate(false)
                .lateMinutes(0)
                .overtimeMinutes(0)
                .build();

        given(attendanceRepository
                .findTopByUserIdAndPunchOutDateIsNullOrderByPunchInDateDesc(USER_ID))
                .willReturn(Optional.of(attendance));

        // when
        // (내부에서 now로 퇴근 시간 + 초과근무 계산 후 updateCheckOut 호출)
        attendanceService.checkOut(USER_ID);

        // then
        // 같은 객체가 수정되었는지 확인 (엔티티 메서드로 상태 변경되었다고 가정)
        assertNotNull(attendance.getPunchOutDate());
        assertTrue(attendance.getOvertimeMinutes() >= 0);
    }

    // 4. 출근 기록 없이 퇴근 찍으면 예외
    @Test
    @DisplayName("아직 출근 기록이 없는 사용자가 퇴근 체크하면 ATTENDANCE_NOT_CHECKED_IN 예외가 발생해야 한다")
    void checkOut_notCheckedIn_shouldThrowException() {
        // given
        given(attendanceRepository
                .findTopByUserIdAndPunchOutDateIsNullOrderByPunchInDateDesc(USER_ID))
                .willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> attendanceService.checkOut(USER_ID));

        assertEquals(ErrorCode.ATTENDANCE_NOT_CHECKED_IN, ex.getErrorCode());
    }
}
