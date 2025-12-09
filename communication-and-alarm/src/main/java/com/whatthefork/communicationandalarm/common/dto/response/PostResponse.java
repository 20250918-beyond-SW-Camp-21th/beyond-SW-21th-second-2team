package com.whatthefork.communicationandalarm.common.dto.response;

import com.whatthefork.communicationandalarm.common.enums.Category;
import com.whatthefork.communicationandalarm.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long postId;
    private Long memberId;
    private String memberName;
    private String title;
    private Category category;
    private Boolean isPinned;
    private Long viewCount;
    private Long commentCount;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post, Long viewCount, Long commentCount) {
        return PostResponse.builder()
                .postId(post.getId())
                .memberId(post.getMemberId())
                .memberName(post.getMemberName())
                .title(post.getTitle())
                .category(post.getCategory())
                .isPinned(post.getIsPinned())
                .viewCount(viewCount)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
