package com.whatthefork.approvalsystem.repository;

import com.whatthefork.approvalsystem.domain.ApprovalHistory;
import com.whatthefork.approvalsystem.enums.ActionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalHistoryRepositoy extends JpaRepository<ApprovalHistory, Long> {

    boolean existsByDocumentAndActorAndActionType(Long documentId, Long actor, ActionTypeEnum actionType);

}
