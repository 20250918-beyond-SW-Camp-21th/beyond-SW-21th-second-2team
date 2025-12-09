package com.whatthefork.attendancetracking.annualLeave.repository;

import com.whatthefork.attendancetracking.annualLeave.domain.AnnualLeaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AnnualLeaveHistoryRepository extends JpaRepository<AnnualLeaveHistory, Long> {

    List<AnnualLeaveHistory> findByMemberIdAndStartDateBetween(Long memberId, LocalDate start, LocalDate end);
}
