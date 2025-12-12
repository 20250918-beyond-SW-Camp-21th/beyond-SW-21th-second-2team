package com.whatthefork.attendancetracking.attendance.domain;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Attendance {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    //출근시간
    @Column(name = "punch_in", nullable = true)
    private LocalDateTime punchInDate;

    //퇴근시간
    @Column(name = "punch_out", nullable = true)
    private LocalDateTime punchOutDate;

    //지각여부
    @Column(name = "is_late", nullable = false)
    private boolean isLate;

    @Column(name = "late_minutes", nullable = false)
    private Integer lateMinutes;

    //초과근무
    @Column(name = "overtime_minutes", nullable = false)
    private Integer overtimeMinutes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Attendance(Long userId, LocalDateTime punchInDate, LocalDateTime punchOutDate, boolean isLate, Integer lateMinutes,Integer overtimeMinutes) {
        this.userId = userId;
        this.punchInDate = punchInDate;
        this.punchOutDate = punchOutDate;
        this.isLate = isLate;
        this.lateMinutes = lateMinutes;
        this.overtimeMinutes = overtimeMinutes;
    }

    public void updateCheckOut(LocalDateTime punchOutDate,Integer overtimeMinutes){
        this.punchOutDate = punchOutDate;
        this.overtimeMinutes = overtimeMinutes;
    }
}
