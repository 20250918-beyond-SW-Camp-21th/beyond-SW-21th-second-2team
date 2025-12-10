package com.whatthefork.communicationandalarm.post.domain;
import com.whatthefork.communicationandalarm.common.BaseEntity;
import com.whatthefork.communicationandalarm.common.enums.Category;
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
@Builder
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

    public static Post create(Long memberId, String memberName, String title, String content, Category category) {

        boolean pinned = (category == Category.ANNOUNCEMENT);

        return Post.builder()
                .memberId(memberId)
                .memberName(memberName)
                .category(category)
                .title(title)
                .content(content)
                .isPinned(false)
                .isDeleted(false)
                .build();
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;

        if (category == Category.ANNOUNCEMENT) {
            this.isPinned = true;
        } else {
            this.isPinned = false;
        }
    }

    public boolean isOwner(Long memberId) {
        return this.memberId.equals(memberId);
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void pin() {
        this.isPinned = true;
    }

    public void unpin() {
        this.isPinned = false;
    }
}
