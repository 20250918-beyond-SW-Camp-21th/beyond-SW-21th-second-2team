package com.whatthefork.attendancetracking.attendance.controller;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import com.whatthefork.attendancetracking.attendance.dto.AttendanceResponse;
import com.whatthefork.attendancetracking.attendance.dto.AttendanceTotalResponse;
import com.whatthefork.attendancetracking.attendance.repository.AttendanceRepository;
import com.whatthefork.attendancetracking.attendance.service.AttendanceService;
import com.whatthefork.attendancetracking.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "출근 등록", description = "출근을 등록합니다.")
    @PostMapping("/checkIn")
    public ResponseEntity<ApiResponse> checkIn(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);

        attendanceService.checkIn(userId);
        return  ResponseEntity.ok(ApiResponse.success("success"));
    }

    @Operation(summary = "퇴근 등록", description = "퇴근을 등록합니다.")
    @PostMapping("/checkOut")
    public ResponseEntity<ApiResponse> checkOut(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        attendanceService.checkOut(userId);
        return  ResponseEntity.ok(ApiResponse.success("success"));
    }

    @Operation(summary = "당일 출퇴근 기록 조회", description = "오늘의 출퇴근 기록을 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<ApiResponse> getToday(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        Optional<AttendanceResponse> todayAttendance = attendanceService.getToday(userId);

        if (todayAttendance.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        return ResponseEntity.ok(ApiResponse.success(todayAttendance.get()));
    }

    @Operation(summary = "이번주 출퇴근 기록 조회", description = "월요일부터 현재까지의 출퇴근 기록을 조회합니다.")
    @GetMapping("/week")
    public ResponseEntity<ApiResponse> getWeek(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        List<AttendanceResponse> responses = attendanceService.getWeek(userId);
        return  ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "월 단위 출퇴근 기록 조회", description = "월 단위로 출퇴근 기록을 조회합니다.")
    @GetMapping("/{year}/{month}")
    public ResponseEntity<ApiResponse> getMonth(@AuthenticationPrincipal String userIds, @PathVariable Integer year, @PathVariable Integer month) {

        Long userId = Long.parseLong(userIds);
        List<AttendanceResponse> responses = attendanceService.getMonth(userId,year,month);
        return  ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "이번달 출근수, 지각, 초과근무 현황 조회", description = "이번달 출근수, 지각, 초과근무 현황을 조회합니다.")
    @GetMapping("/total")
    public ResponseEntity<ApiResponse> getTotal(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        Optional<AttendanceTotalResponse> total = attendanceService.getTotal(userId);
        return  ResponseEntity.ok(ApiResponse.success(total));
    }

    @Operation(summary = "지정 날짜 출퇴근 기록 조회", description = "지정한 날짜의 출퇴근 기록을 조회합니다.")
    @GetMapping("/{year}/{month}/{day}")
    public ResponseEntity<ApiResponse> getDay(@AuthenticationPrincipal String userIds, @PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day) {

        Long userId = Long.parseLong(userIds);
        Optional<AttendanceResponse> response = attendanceService.getDay(userId,year,month,day);
        return  ResponseEntity.ok(ApiResponse.success(response));
    }



}
