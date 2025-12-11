package com.whatthefork.attendancetracking.attendance.repository;

import com.whatthefork.attendancetracking.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("""
            SELECT a 
            FROM Attendance a 
            WHERE a.userId = :userId
            AND a.punchInDate BETWEEN :start AND :end
        """)
    Optional<Attendance> findAttendanceByUserId(Long userId, LocalDateTime start, LocalDateTime end);

    Optional<Attendance> findTopByUserIdAndPunchOutDateIsNullOrderByPunchInDateDesc(Long userId);

    Optional<Attendance> findByUserIdAndPunchInDateBetweenOrderByPunchInDateAsc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Attendance> findAllByUserIdAndPunchInDateBetweenOrderByPunchInDateAsc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    boolean findAttendanceByUserIdAndPunchOutDate(Long userId, LocalDateTime punchOutDate);
}
