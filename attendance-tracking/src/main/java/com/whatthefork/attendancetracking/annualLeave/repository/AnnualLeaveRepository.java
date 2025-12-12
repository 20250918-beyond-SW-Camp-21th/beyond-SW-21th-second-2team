package com.whatthefork.attendancetracking.annualLeave.repository;

import com.whatthefork.attendancetracking.annualLeave.domain.AnnualLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnualLeaveRepository extends JpaRepository<AnnualLeave, Integer> {

    AnnualLeave findByMemberIdAndYear(Long memberId, Integer year);
}
