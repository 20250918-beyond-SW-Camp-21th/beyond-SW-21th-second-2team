package com.whatthefork.approvalsystem.domain;

import com.whatthefork.approvalsystem.enums.ActionTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ApprovalDocument 객체 간접 참조
    @Column(name = "doc_id", nullable = false)
    private Long document;

    // 행위자, Member 객체 간접 참조
    @Column(name = "actor_id", nullable = false)
    private Long actor;

    @Column(nullable = false)
    private LocalDateTime openDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionTypeEnum actionType; // ENUM: CREATE, SUBMIT, READ, APPROVE, REJECT, UPDATE, CANCEL;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // 해당 결재의 직전 로그 확인용 컬럼
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_parent_id")
    private ApprovalHistory parent;

    @Builder
    public ApprovalHistory(Long document, Long actor, ActionTypeEnum actionType, String comment, ApprovalHistory parent) {
        this.document = document;
        this.actor = actor;
        this.openDate = LocalDateTime.now();
        this.actionType = actionType;
        this.comment = comment;
        this.parent = parent;
    }
}
