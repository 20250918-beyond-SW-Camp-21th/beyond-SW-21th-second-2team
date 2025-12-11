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
import java.time.temporal.ChronoUnit;


@Getter
@NoArgsConstructor
@Entity
public class AnnualLeaveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "used_leave", nullable = false)
    private Integer usedLeave = 0;

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
    public AnnualLeaveHistory(Long memberId,Integer usedLeave, LocalDate startDate, LocalDate endDate, Long approverId) {
        this.memberId = memberId;
        this.usedLeave = usedLeave;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approverId = approverId;
    }

}
