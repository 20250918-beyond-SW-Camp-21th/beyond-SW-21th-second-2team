package com.approvalsystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ApprovalReferrer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ApprovalDocument 객체 간접 참조
    @Column(name = "doc_id", nullable = false)
    private Long document;

    // 참조자(결재 권한 없음, 읽을 수만 있으며 결재선 상태에 영향 X), Member 객체 간접 참조
    @Column(name = "referrer_id", nullable = false)
    private Long referrer;

    private LocalDateTime viewedAt;

    @Builder
    public ApprovalReferrer(Long document, Long referrer, LocalDateTime viewedAt) {
        this.document = document;
        this.referrer = referrer;
        this.viewedAt = viewedAt;
    }
}
