package com.whatthefork.attendancetracking.attendance.controller;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import com.whatthefork.attendancetracking.attendance.dto.AttendanceResponse;
import com.whatthefork.attendancetracking.attendance.dto.AttendanceTotalResponse;
import com.whatthefork.attendancetracking.attendance.repository.AttendanceRepository;
import com.whatthefork.attendancetracking.attendance.service.AttendanceService;
import com.whatthefork.attendancetracking.common.ApiResponse;
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

    @PostMapping("/checkIn")
    public ResponseEntity<ApiResponse> checkIn(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);

        attendanceService.checkIn(userId);
        return  ResponseEntity.ok(ApiResponse.success("success"));
    }

    @PostMapping("/checkOut")
    public ResponseEntity<ApiResponse> checkOut(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        attendanceService.checkOut(userId);
        return  ResponseEntity.ok(ApiResponse.success("success"));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse> getToday(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        Optional<AttendanceResponse> todayAttendance = attendanceService.getToday(userId);

        if (todayAttendance.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        return ResponseEntity.ok(ApiResponse.success(todayAttendance.get()));
    }

    @GetMapping("/week")
    public ResponseEntity<ApiResponse> getWeek(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        List<AttendanceResponse> responses = attendanceService.getWeek(userId);
        return  ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<ApiResponse> getMonth(@AuthenticationPrincipal String userIds, @PathVariable Integer year, @PathVariable Integer month) {

        Long userId = Long.parseLong(userIds);
        List<AttendanceResponse> responses = attendanceService.getMonth(userId,year,month);
        return  ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse> getTotal(@AuthenticationPrincipal String userIds) {

        Long userId = Long.parseLong(userIds);
        Optional<AttendanceTotalResponse> total = attendanceService.getTotal(userId);
        return  ResponseEntity.ok(ApiResponse.success(total));
    }

    @GetMapping("/{year}/{month}/{day}")
    public ResponseEntity<ApiResponse> getDay(@AuthenticationPrincipal String userIds, @PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day) {

        Long userId = Long.parseLong(userIds);
        Optional<AttendanceResponse> response = attendanceService.getDay(userId,year,month,day);
        return  ResponseEntity.ok(ApiResponse.success(response));
    }



}
