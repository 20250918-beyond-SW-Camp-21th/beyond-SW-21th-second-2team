package com.whatthefork.approvalsystem.domain;

import com.whatthefork.approvalsystem.enums.LineStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ApprovalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    // ApprovalDocument 객체 간접 참조
    @Column(name = "doc_id", nullable = false)
    private Long document;

    // 결재자, Member 객체 간접 참조
    @Column(name = "approver_id", nullable = false)
    private Long approver;

    @Column(nullable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineStatusEnum lineStatus;  // ENUM: WAIT, APPROVED, REJECTED, CANCELED

    private LocalDateTime approvedAt;

    @Builder
    public ApprovalLine(Long document, Long approver, int sequence, LineStatusEnum lineStatus, LocalDateTime approvedAt) {
        this.document = document;
        this.approver = approver;
        this.sequence = sequence;
        this.lineStatus = lineStatus;
        this.approvedAt = approvedAt;
    }

    public void approve() {
        this.lineStatus = LineStatusEnum.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        this.lineStatus = LineStatusEnum.REJECTED;
        this.approvedAt = LocalDateTime.now();
    }
}
