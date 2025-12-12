package com.whatthefork.communicationandalarm.postTest;

import com.whatthefork.communicationandalarm.client.MemberClient;
import com.whatthefork.communicationandalarm.comment.domain.CommentRepository;
import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.dto.request.CreatePostRequest;
import com.whatthefork.communicationandalarm.common.dto.response.GetPostResponse;
import com.whatthefork.communicationandalarm.common.dto.response.PostResponse;
import com.whatthefork.communicationandalarm.common.dto.response.UserDTO;
import com.whatthefork.communicationandalarm.common.dto.response.UserDetailResponse;
import com.whatthefork.communicationandalarm.common.enums.Category;
import com.whatthefork.communicationandalarm.common.exception.GlobalException;
import com.whatthefork.communicationandalarm.common.utils.Page;
import com.whatthefork.communicationandalarm.post.domain.Post;
import com.whatthefork.communicationandalarm.post.domain.PostRepository;
import com.whatthefork.communicationandalarm.post.domain.PostService;
import com.whatthefork.communicationandalarm.post.domain.PostViewLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 서비스 테스트")
public class PostTests {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberClient memberClient;

    @Nested
    @DisplayName("게시글 등록")
    class CreatePost {

        @Test
        @DisplayName("관리자는 공지사항 게시글을 등록할 수 있으며 자동으로 상단 고정된다.")
        void adminCanCreateAnnouncementAndPinned() {
            // given
            String adminId = "1"; // 관리자
            CreatePostRequest request = org.mockito.Mockito.mock(CreatePostRequest.class);
            given(request.getCategory()).willReturn(Category.ANNOUNCEMENT);
            given(request.getTitle()).willReturn("공지");
            given(request.getContent()).willReturn("내용");

            UserDTO adminUser = org.mockito.Mockito.mock(UserDTO.class);
            given(adminUser.getName()).willReturn("관리자");
            given(adminUser.getRole()).willReturn("ADMIN");

            UserDetailResponse userDetail = org.mockito.Mockito.mock(UserDetailResponse.class);
            given(userDetail.getUser()).willReturn(adminUser);

            ApiResponse<UserDetailResponse> apiResponse =
                    Mockito.mock(ApiResponse.class);
            given(apiResponse.getData()).willReturn(userDetail);

            given(memberClient.getUserDetail(adminId)).willReturn(apiResponse);

            // PostRepository.save 가 호출되면 전달받은 Post 를 그대로 반환하도록 설정
            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            given(postRepository.save(any(Post.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            postService.create(adminId, request);

            // then
            verify(postRepository).save(postCaptor.capture());
            Post savedPost = postCaptor.getValue();

            assertThat(savedPost.getCategory()).isEqualTo(Category.ANNOUNCEMENT);
            assertThat(savedPost.getIsPinned()).isTrue();     // 공지는 자동 상단 고정
            assertThat(savedPost.getIsDeleted()).isFalse();   // 기본값 false
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 공지사항을 등록하면 예외가 발생한다.")
        void nonAdminCannotCreateAnnouncement() {
            // given
            String memberId = "2";

            CreatePostRequest request = org.mockito.Mockito.mock(CreatePostRequest.class);
            given(request.getCategory()).willReturn(Category.ANNOUNCEMENT);

            UserDTO normalUser = org.mockito.Mockito.mock(UserDTO.class);
            given(normalUser.getName()).willReturn("일반유저");
            given(normalUser.getRole()).willReturn("USER");

            UserDetailResponse userDetail = org.mockito.Mockito.mock(UserDetailResponse.class);
            given(userDetail.getUser()).willReturn(normalUser);

            ApiResponse<UserDetailResponse> apiResponse =
                    Mockito.mock(ApiResponse.class);
            given(apiResponse.getData()).willReturn(userDetail);

            given(memberClient.getUserDetail(memberId)).willReturn(apiResponse);

            // when & then
            assertThatThrownBy(() -> postService.create(memberId, request))
                    .isInstanceOf(GlobalException.class);
        }
    }

    @Nested
    @DisplayName("게시글 단건 조회")
    class GetPost {

        @Test
        @DisplayName("게시글 단건 조회 시 조회수와 댓글 수를 포함한 응답을 반환한다.")
        void getPostWithViewAndCommentCount() {
            // given
            String memberId = "1";
            Long postId = 5L;

            Post post = Post.builder()
                    .id(postId)
                    .memberId(2L)
                    .memberName("작성자")
                    .category(Category.GENERAL)
                    .title("제목")
                    .content("내용")
                    .isPinned(false)
                    .isDeleted(false)
                    .build();

            given(postRepository.findByIdAndIsDeletedFalse(postId))
                    .willReturn(Optional.of(post));

            given(postRepository.save(any(PostViewLog.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            given(postRepository.countViewsByPostId(postId)).willReturn(5L);
            given(commentRepository.countByPostId(postId)).willReturn(3L);

            // when
            GetPostResponse response = postService.getPost(memberId, postId);

            // then
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getViewCount()).isEqualTo(5L);
            assertThat(response.getCommentCount()).isEqualTo(3L);

            verify(postRepository).findByIdAndIsDeletedFalse(postId);
            verify(postRepository).countViewsByPostId(postId);
            verify(commentRepository).countByPostId(postId);
        }
    }

    @Nested
    @DisplayName("게시글 리스트 조회")
    class GetPosts {

        @Test
        @DisplayName("offset/limit 기반으로 게시글 리스트를 조회하고, hasNext 여부를 계산한다.")
        void getPostsWithPagination() {
            // given
            int offset = 0;
            int limit = 20;

            Post post1 = Post.builder()
                    .id(1L)
                    .memberId(1L)
                    .memberName("작성자1")
                    .category(Category.GENERAL)
                    .title("제목1")
                    .content("내용1")
                    .isPinned(false)
                    .isDeleted(false)
                    .build();

            Post post2 = Post.builder()
                    .id(2L)
                    .memberId(2L)
                    .memberName("작성자2")
                    .category(Category.ANNOUNCEMENT)
                    .title("제목2")
                    .content("내용2")
                    .isPinned(true)
                    .isDeleted(false)
                    .build();

            List<Post> posts = List.of(post1, post2);

            // PostRepository.findAllActive 가 Pageable 을 받음
            // Page 객체를 만들어 content 리스트만 꺼내 쓰도록 가정
            given(postRepository.findAllActive(any(PageRequest.class)))
                    .willReturn(posts);

            given(postRepository.countViewsByPostId(1L)).willReturn(0L);
            given(postRepository.countViewsByPostId(2L)).willReturn(0L);
            given(commentRepository.countByPostId(anyLong())).willReturn(0L);

            // when
            Page<PostResponse> page = postService.getPosts(offset, limit);

            // then
            assertThat(page.getContents()).hasSize(2);
            assertThat(page.getHasNext()).isFalse();   // size(2) < limit(20) 이므로 hasNext = false

            PostResponse first = page.getContents().get(0);
            assertThat(first.getPostId()).isEqualTo(1L);
            assertThat(first.getTitle()).isEqualTo("제목1");
        }
    }
}

