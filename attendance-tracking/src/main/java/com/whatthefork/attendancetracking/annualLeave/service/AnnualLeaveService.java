package com.whatthefork.attendancetracking.annualLeave.service;


import com.whatthefork.attendancetracking.annualLeave.domain.AnnualLeave;
import com.whatthefork.attendancetracking.annualLeave.domain.AnnualLeaveHistory;
import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveHistoryResponse;
import com.whatthefork.attendancetracking.annualLeave.dto.AnnualLeaveResponse;
import com.whatthefork.attendancetracking.annualLeave.repository.AnnualLeaveHistoryRepository;
import com.whatthefork.attendancetracking.annualLeave.repository.AnnualLeaveRepository;
import com.whatthefork.attendancetracking.common.error.BusinessException;
import com.whatthefork.attendancetracking.common.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnualLeaveService {

    private final AnnualLeaveRepository annualLeaveRepository;
    private final AnnualLeaveHistoryRepository annualLeaveHistoryRepository;


    //연차 년도 현황 조회
    @Transactional
    public Optional<AnnualLeaveResponse> getAnnualLeave(Long memberId, Integer year) {

        AnnualLeave annualLeave = annualLeaveRepository.findByMemberIdAndYear(memberId, year)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNUAL_LEAVE_NOT_FOUND));

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<AnnualLeaveHistory> histories = annualLeaveHistoryRepository.findByMemberIdAndStartDateBetween(memberId, start, end);

        int usedLeave = histories
                .stream()
                .mapToInt(AnnualLeaveHistory::getUsedLeave)
                .sum();

        int remainingLeave = annualLeave.getTotalLeave() - usedLeave;

        AnnualLeaveResponse annualLeaveResponse = AnnualLeaveResponse.builder()
                .memberId(memberId)
                .year(year)
                .totalLeave(annualLeave.getTotalLeave())
                .usedLeave(usedLeave)
                .remainingLeave(remainingLeave)
                .build();

        return Optional.of(annualLeaveResponse);
    }


    //연차 결재 완료 리스트 조회
    @Transactional
    public List<AnnualLeaveHistoryResponse> getAnnualLeaveHistories(Long memberId, Integer year) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<AnnualLeaveHistory> histories =
                annualLeaveHistoryRepository.findByMemberIdAndStartDateBetween(memberId, start, end);

        return histories.stream()
                .map(h -> AnnualLeaveHistoryResponse.builder()
                        .id(h.getId())
                        .memberId(h.getMemberId())
                        .usedLeave(h.getUsedLeave())
                        .startDate(h.getStartDate())
                        .endDate(h.getEndDate())
                        .approverId(h.getApproverId())
                        .createdAt(h.getCreatedAt())
                        .build()
                )
                .toList();
    }
}
