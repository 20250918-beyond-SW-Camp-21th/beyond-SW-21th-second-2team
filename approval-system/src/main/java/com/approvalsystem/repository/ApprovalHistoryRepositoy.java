package com.approvalsystem.repository;

import com.approvalsystem.domain.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalHistoryRepositoy extends JpaRepository<ApprovalHistory, Long> {

}
