package com.whatthefork.communicationandalarm.commentTest;

import com.whatthefork.communicationandalarm.client.MemberClient;
import com.whatthefork.communicationandalarm.comment.domain.Comment;
import com.whatthefork.communicationandalarm.comment.domain.CommentRepository;
import com.whatthefork.communicationandalarm.comment.domain.CommentService;
import com.whatthefork.communicationandalarm.common.ApiResponse;
import com.whatthefork.communicationandalarm.common.dto.request.CreateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.request.UpdateCommentRequest;
import com.whatthefork.communicationandalarm.common.dto.response.CommentResponse;
import com.whatthefork.communicationandalarm.common.dto.response.UserDTO;
import com.whatthefork.communicationandalarm.common.dto.response.UserDetailResponse;
import com.whatthefork.communicationandalarm.common.exception.GlobalException;
import com.whatthefork.communicationandalarm.common.utils.Page;
import com.whatthefork.communicationandalarm.post.domain.Post;
import com.whatthefork.communicationandalarm.post.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("댓글 서비스 테스트")
public class CommentTests {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberClient memberClient;

    @Mock
    private PostRepository postRepository;

    @Nested
    @DisplayName("댓글 등록")
    class CreateComment {

        @Test
        @DisplayName("루트 댓글(부모 없음) 등록 성공")
        void createRootComment_success() {

            // given
            String memberId = "1";
            Long postId = 10L;

            Post post = Mockito.mock(Post.class);
            given(postRepository.findByIdAndIsDeletedFalse(postId))
                    .willReturn(Optional.of(post));

            CreateCommentRequest request = org.mockito.Mockito.mock(CreateCommentRequest.class);
            given(request.getParentCommentId()).willReturn(null);
            given(request.getContent()).willReturn("루트 댓글입니다.");

            UserDTO user = Mockito.mock(UserDTO.class);
            given(user.getId()).willReturn(1L);
            given(user.getName()).willReturn("테스트유저");

            UserDetailResponse detail = Mockito.mock(UserDetailResponse.class);
            given(detail.getUser()).willReturn(user);

            ApiResponse<UserDetailResponse> api = Mockito.mock(ApiResponse.class);

            lenient().when(api.getData()).thenReturn(detail);

            given(memberClient.getUserDetail(anyString()))
                    .willReturn(api);

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            given(commentRepository.save(any(Comment.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            commentService.create(memberId, postId, request);

            // then
            verify(commentRepository).save(captor.capture());
            Comment saved = captor.getValue();

            assertThat(saved.getMemberId()).isEqualTo(1L);
            assertThat(saved.getMemberName()).isEqualTo("테스트유저");
            assertThat(saved.getPostId()).isEqualTo(postId);
            assertThat(saved.getParentCommentId()).isNull();
            assertThat(saved.getDepth()).isEqualTo(0L);
            assertThat(saved.getContent()).isEqualTo("루트 댓글입니다.");
        }

        @Test
        @DisplayName("부모 댓글 depth가 최대 깊이 이면 예외 발생")
        void createReply_exceedsMaxDepth_fail() {
            // given
            Long postId = 10L;
            Long parentId = 99L;

            Comment parent = new Comment(
                    parentId,
                    1L,
                    postId,
                    null,
                    "테스트",
                    2L,
                    "부모 댓글",
                    false
            );

            given(commentRepository.findByCommentIdAndIsDeletedFalse(parentId))
                    .willReturn(Optional.of(parent));

            // when & then
            CreateCommentRequest request = Mockito.mock(CreateCommentRequest.class);
            given(request.getParentCommentId()).willReturn(parentId);

            assertThatThrownBy(() ->
                    commentService.create("1", postId, request)
            ).isInstanceOf(GlobalException.class);
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateComment {

        @Test
        @DisplayName("작성자가 자신의 댓글을 수정한다.")
        void updateComment_success() {

            Comment comment = new Comment(
                    10L,
                    1L,
                    100L,

                    null,
                    "작성자",
                    0L,

                    "기존 내용",
                    false
            );

            given(commentRepository.findByCommentIdAndIsDeletedFalse(10L))
                    .willReturn(Optional.of(comment));

            UpdateCommentRequest request = Mockito.mock(UpdateCommentRequest.class);
            given(request.getContent()).willReturn("수정된 내용");

            commentService.update("1", 10L, request);

            assertThat(comment.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("작성자가 아니면 댓글 수정 실패")
        void updateComment_notOwner_fail() {

            Comment comment = new Comment(
                    10L,
                    1L,
                    100L,

                    null,
                    "작성자",
                    0L,
                    "내용",
                    false
            );

            given(commentRepository.findByCommentIdAndIsDeletedFalse(10L))
                    .willReturn(Optional.of(comment));

            UpdateCommentRequest request = Mockito.mock(UpdateCommentRequest.class);

            assertThatThrownBy(() -> commentService.update("2", 10L, request))
                    .isInstanceOf(GlobalException.class);
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {

        @Test
        @DisplayName("대댓글이 없으면 삭제 성공")
        void deleteComment_success() {

            Comment comment = new Comment(
                    10L,
                    1L,
                    100L,
                    null,
                    "작성자",
                    0L,
                    "삭제될 댓글",
                    false
            );

            given(commentRepository.findByCommentIdAndIsDeletedFalse(10L))
                    .willReturn(Optional.of(comment));

            given(commentRepository.existsByParentCommentIdAndIsDeletedFalse(10L))
                    .willReturn(false);

            commentService.delete("1", 10L);

            assertThat(comment.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("작성자가 아니면 삭제 실패")
        void deleteComment_notOwner_fail() {

            Comment comment = new Comment(
                    10L,
                    1L,
                    100L,
                    null,
                    "작성자",
                    0L,
                    "내용",
                    false
            );

            given(commentRepository.findByCommentIdAndIsDeletedFalse(10L))
                    .willReturn(Optional.of(comment));

            assertThatThrownBy(() -> commentService.delete("5", 10L))
                    .isInstanceOf(GlobalException.class);
        }

        @Test
        @DisplayName("대댓글이 있으면 삭제 실패")
        void deleteComment_hasReply_fail() {

            Comment comment = new Comment(
                    10L, 1L, 100L,
                    null, "작성자", 0L,
                    "삭제될 댓글", false
            );

            given(commentRepository.findByCommentIdAndIsDeletedFalse(10L))
                    .willReturn(Optional.of(comment));

            given(commentRepository.existsByParentCommentIdAndIsDeletedFalse(10L))
                    .willReturn(true);

            assertThatThrownBy(() -> commentService.delete("1", 10L))
                    .isInstanceOf(GlobalException.class);
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회")
    class GetComments {

        @Test
        @DisplayName("댓글 페이지 조회 성공")
        void getComments_success() {

            Long postId = 100L;

            Comment c1 = new Comment(
                    1L,
                    1L,
                    postId,
                    null,
                    "작성자1",
                    0L,
                    "댓글1",
                    false
            );

            Comment c2 = new Comment(
                    2L,
                    2L,
                    postId,
                    null,
                    "작성자2",
                    0L,
                    "댓글2",
                    false
            );

            List<Comment> list = new ArrayList<>();
            list.add(c1);
            list.add(c2);

            given(commentRepository.findPageByPostId(eq(postId), any(Pageable.class)))
                    .willReturn(list);

            Page<CommentResponse> page = commentService.getComments(postId, 0, 10);

            assertThat(page.getContents()).hasSize(2);
            assertThat(page.getHasNext()).isFalse();
        }
    }
}
