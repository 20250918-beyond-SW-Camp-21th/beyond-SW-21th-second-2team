package com.ohgiraffers.communicationandalarm.post.domain;
import com.ohgiraffers.communicationandalarm.common.BaseEntity;
import com.ohgiraffers.communicationandalarm.common.enums.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(nullable=false)
    private Category category;

    @Column(name = "title", length = 20, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_pinned", nullable = false)
    public Boolean isPinned;

    @Column(name = "is_deleted", nullable = false)
    public Boolean isDeleted = false;

    @Builder
    public Post(Long memberId, String memberName, Category category, String title, String content, Boolean isPinned) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.category = category;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.isDeleted = false;
    }

    public static Post create(Long memberId, String memberName, String title, String content, Category category) {
        return Post.builder()
                .memberId(memberId)
                .memberName(memberName)
                .category(category)
                .title(title)
                .content(content)
                .isPinned(false)
                .build();
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public boolean isOwner(Long memberId) {
        return this.memberId.equals(memberId);
    }

    public void delete() {
        this.isDeleted = true;
    }
}
