package com.whatthefork.attendancetracking.attendance.service;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import com.whatthefork.attendancetracking.attendance.dto.AttendanceResponse;
import com.whatthefork.attendancetracking.attendance.dto.AttendanceTotalResponse;
import com.whatthefork.attendancetracking.attendance.repository.AttendanceRepository;
import com.whatthefork.attendancetracking.common.error.BusinessException;
import com.whatthefork.attendancetracking.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    // (예시)회사 지정 근무 시간
    private static final LocalTime START_TIME = LocalTime.of(9,0);
    private static final LocalTime END_TIME = LocalTime.of(18,0);

    //출근 찍기 가능한 시간(고민)
    private static final LocalTime CHECK_IN_ALLOWED_TIME = LocalTime.of(5,0);

    //출근
    @Transactional
    public void checkIn(Long userId) {

        LocalDateTime now = LocalDateTime.now();

        LocalDate today = now.toLocalDate();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        LocalTime nowTime = now.toLocalTime();

        boolean isLate = now.toLocalTime().isAfter(START_TIME);

        if(nowTime.isBefore(CHECK_IN_ALLOWED_TIME)){
            throw new BusinessException(ErrorCode.ATTENDANCE_NOT_CHECK_IN_TIME);
        }

        if(attendanceRepository.findAttendanceByUserId(userId,start,end).isPresent()){
            throw new BusinessException(ErrorCode.ATTENDANCE_ALREADY_CHECKED_IN);
        }

        int lateMinutes = isLate ?
                (int)Duration.between(START_TIME,nowTime).toMinutes()
                : 0;


        Attendance attendance = Attendance.builder()
                .userId(userId)
                .punchInDate(now)
                .punchOutDate(null)
                .isLate(isLate)
                .lateMinutes(lateMinutes)
                .overtimeMinutes(0)
                .build();

        this.attendanceRepository.save(attendance);


    }

    //퇴근
    @Transactional
    public void checkOut(Long userId){

        Attendance attendance = attendanceRepository
                .findTopByUserIdAndPunchOutDateIsNullOrderByPunchInDateDesc(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.ATTENDANCE_NOT_CHECKED_IN));

        LocalDateTime now = LocalDateTime.now();

        LocalDate workDate = attendance.getPunchInDate().toLocalDate();
        LocalDateTime endWorkDate = workDate.atTime(END_TIME);

        int overTimeMinutes = 0;
        if(now.isAfter(endWorkDate)){
            overTimeMinutes = (int)Duration.between(endWorkDate,now).toMinutes();
        }

        attendance.updateCheckOut(now,overTimeMinutes);
    }

    //오늘 출퇴근 현황
    public Optional<AttendanceResponse> getToday(Long userId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return attendanceRepository
                .findByUserIdAndPunchInDateBetweenOrderByPunchInDateAsc(userId,start,end)
                .map(AttendanceResponse::from);

    }

    //이번주 출퇴근 현황
    public List<AttendanceResponse> getWeek(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.plusDays(1).atStartOfDay();

        return attendanceRepository
                .findAllByUserIdAndPunchInDateBetweenOrderByPunchInDateAsc(userId,start,end)
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    //지정 year,month의 출퇴근 현황
    public List<AttendanceResponse> getMonth(Long userId, int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate firstDayNextMonth = firstDay.plusMonths(1);

        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = firstDayNextMonth.atStartOfDay();

        return attendanceRepository
                .findAllByUserIdAndPunchInDateBetweenOrderByPunchInDateAsc(userId, start, end)
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    // 이번달 출근수, 지각, 초과근무 현황
    public Optional<AttendanceTotalResponse> getTotal(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = LocalDate.of(today.getYear(), today.getMonth(), 1);

        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<Attendance> attendances = attendanceRepository.findAllByUserIdAndPunchInDateBetweenOrderByPunchInDateAsc(userId,start,end);

        int totalAttendanceCount = attendances.size();
        int totalLateCount = (int)attendances.stream().filter(Attendance::isLate).count();
        int totalLateMinutes = attendances.stream().mapToInt(Attendance::getLateMinutes).sum();
        int totalOverTimeMinutes = attendances.stream().filter(a -> a.getPunchOutDate() != null).mapToInt(Attendance::getOvertimeMinutes).sum();

        AttendanceTotalResponse response = AttendanceTotalResponse.builder()
                .totalAttendanceCount("이번달 출근한 날 : " + totalAttendanceCount + "번")
                .totalLate("지각한 날 : " + totalLateCount + "번 (" + totalLateMinutes + "분)")
                .totalOverTimeMinutes("총 초과근무 : " + totalOverTimeMinutes + "분")
                .build();

        return Optional.of(response);
    }


    public List<AttendanceResponse> getDay(Long userId, Integer year, Integer month, Integer day) {

        LocalDate userDay = LocalDate.of(year,month,day);
        LocalDateTime start = userDay.atStartOfDay();
        LocalDateTime end = userDay.plusDays(1).atStartOfDay();

        return attendanceRepository
                .findAttendanceByUserId(userId,start,end)
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }
}
