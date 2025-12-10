package com.whatthefork.approvalsystem.domain;

import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@SQLRestriction("is_deleted = false")
public class ApprovalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기안자
    @Column(name = "drafter_id", nullable = false)
    private Long drafter;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_status", nullable = false)
    private DocStatusEnum docStatus; // ENUM: TEMP, IN_PROGRESS, APPROVED, REJECTED

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Version
    private Long version;

    @Column(name = "current_seq", nullable = false)
    private int currentSequence = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "start_vacation")
    private LocalDate startVacationDate;

    @Column(name = "end_vacation")
    private LocalDate endVacationDate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Builder
    public ApprovalDocument(Long drafter, DocStatusEnum docStatus, String title, String content, Long version, int currentSequence, LocalDateTime createdAt, LocalDate startVacationDate, LocalDate endVacationDate) {
        this.drafter = drafter;
        this.docStatus = docStatus;
        this.title = title;
        this.content = content;
        this.version = version;
        this.currentSequence = currentSequence;
        this.createdAt = createdAt;
        this.startVacationDate = startVacationDate;
        this.endVacationDate = endVacationDate;
    }

    public void submit() {
        this.docStatus = DocStatusEnum.IN_PROGRESS;
    }

    public void updateDocument(String title, String content, LocalDate startVacationDate, LocalDate endVacationDate) {
        this.title = title;
        this.content = content;
        this.startVacationDate = startVacationDate;
        this.endVacationDate = endVacationDate;
    }

    public void deleteDocument() {
        this.isDeleted = true;
    }

    public boolean isSameDrafter(Long userId) {
        return this.drafter.equals(userId);
    }

    public boolean isTempStatus() {
        return this.docStatus == DocStatusEnum.TEMP;
    }

    public void updateProgress() {
        this.docStatus = DocStatusEnum.IN_PROGRESS;
    }

    public void updateTemp() {
        this.docStatus = DocStatusEnum.TEMP;
    }

    public void nextSequence() {
        this.currentSequence++;
    }

    public void completeApproval() {
        this.docStatus = DocStatusEnum.APPROVED;
    }

    public void rejectApproval() {
        this.docStatus = DocStatusEnum.REJECTED;
    }
}