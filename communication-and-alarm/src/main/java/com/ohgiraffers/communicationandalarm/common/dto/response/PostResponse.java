package com.ohgiraffers.communicationandalarm.common.dto.response;

import com.ohgiraffers.communicationandalarm.common.enums.Category;
import com.ohgiraffers.communicationandalarm.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private Long memberId;
    private String  memberName;
    private Category category;
    private String title;
    private String content;
    private Boolean isPinned;
    private Long viewCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post, Long viewCount, Long commentCount) {
        return PostResponse.builder()
                .id(post.getId())
                .memberId(post.getMemberId())
                .memberName(post.getMemberName())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .isPinned(post.getIsPinned())
                .viewCount(viewCount)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
