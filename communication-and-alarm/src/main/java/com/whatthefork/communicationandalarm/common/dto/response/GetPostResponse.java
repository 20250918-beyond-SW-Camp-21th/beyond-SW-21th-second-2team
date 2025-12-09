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
public class GetPostResponse {

    private Long postId;
    private Long memberId;
    private String memberName;
    private Category category;
    private String title;
    private String content;
    private Boolean isPinned;
    private Long viewCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetPostResponse of(Post post, Long viewCount, Long commentCount) {
        return GetPostResponse.builder()
                .postId(post.getId())
                .memberId(post.getMemberId())
                .memberName(post.getMemberName())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .viewCount(viewCount)
                .commentCount(commentCount)
                .isPinned(post.getIsPinned())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
