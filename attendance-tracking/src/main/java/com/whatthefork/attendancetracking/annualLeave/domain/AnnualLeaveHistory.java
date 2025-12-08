package com.whatthefork.attendancetracking.annualLeave.domain;

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
public class AnnualLeaveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "used_leave", nullable = false)
    private Double usedLeave = 0.0;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "approver_id", nullable = true)
    private Long approverId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AnnualLeaveHistory(Long userId, Double usedLeave, LocalDate startDate, LocalDate endDate, Long approverId) {
        this.userId = userId;
        this.usedLeave = usedLeave != null ? usedLeave : 0.0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approverId = approverId;
    }
}
