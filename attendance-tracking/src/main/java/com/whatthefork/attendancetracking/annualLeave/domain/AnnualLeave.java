package com.whatthefork.attendancetracking.annualLeave.domain;

import com.whatthefork.attendancetracking.common.error.BusinessException;
import com.whatthefork.attendancetracking.common.error.ErrorCode;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_annual_leave_user_year",
        columnNames = {"member_id", "year"}
))
public class AnnualLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    //연초에 제공되는 연차값
    @Column(name = "total_leave", nullable = false)
    private Integer totalLeave;

    //사용한 연차 누적값
    @Column(name = "used_leave", nullable = false)
    private Integer usedLeave = 0;

    //연차 제공되는 기준 년도
    @Column(name = "year", nullable = false)
    private Integer year;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AnnualLeave(Long memberId, Integer totalLeave, Integer usedLeave, Integer year) {
        this.memberId = memberId;
        this.totalLeave = totalLeave;
        this.usedLeave = usedLeave == null ? 0 : usedLeave;
        this.year = year;
    }

    public int getRemainingLeave() {
        return this.totalLeave - this.usedLeave;
    }

    public void decreaseAnnual(LocalDate startDate, LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new BusinessException(ErrorCode.ANNUAL_LEAVE_INVALID_PERIOD);}

        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (days <= 0) {
            throw new BusinessException(ErrorCode.INVALID_ANNUAL_LEAVE_USAGE);
        }

        int remaining = getRemainingLeave();

        if (remaining < days) {
            throw new BusinessException(ErrorCode.ANNUAL_LEAVE_INSUFFICIENT);
        }

        this.usedLeave += days;
    }


}
