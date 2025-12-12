package com.whatthefork.approvalsystem.service;

import com.whatthefork.approvalsystem.common.ApiResponse;
import com.whatthefork.approvalsystem.common.error.BusinessException;
import com.whatthefork.approvalsystem.common.error.ErrorCode;
import com.whatthefork.approvalsystem.domain.ApprovalDocument;
import com.whatthefork.approvalsystem.domain.ApprovalLine;
import com.whatthefork.approvalsystem.domain.ApprovalReferrer;
import com.whatthefork.approvalsystem.dto.request.CreateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.response.DocumentDetailResponseDto;
import com.whatthefork.approvalsystem.enums.DocStatusEnum;
import com.whatthefork.approvalsystem.feign.client.UserFeignClient;
import com.whatthefork.approvalsystem.feign.dto.UserDetailResponse;
import com.whatthefork.approvalsystem.feign.dto.UserDto;
import com.whatthefork.approvalsystem.repository.ApprovalDocumentRepository;
import com.whatthefork.approvalsystem.repository.ApprovalHistoryRepositoy;
import com.whatthefork.approvalsystem.repository.ApprovalLineRepository;
import com.whatthefork.approvalsystem.repository.ApprovalReferrerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @InjectMocks
    DocumentService documentService;

    @Mock ApprovalDocumentRepository approvalDocumentRepository;
    @Mock ApprovalLineRepository approvalLineRepository;
    @Mock ApprovalHistoryRepositoy approvalHistoryRepositoy;
    @Mock ApprovalReferrerRepository approvalReferrerRepository;
    @Mock UserFeignClient userFeignClient;

    private CreateDocumentRequestDto createRequest(List<Long> approvers, List<Long> referrers) {
        CreateDocumentRequestDto dto = new CreateDocumentRequestDto();
        dto.setTitle("휴가 신청");
        dto.setContent("내용");
        dto.setStartVacationDate(LocalDate.now());
        dto.setEndVacationDate(LocalDate.now().plusDays(1));
        dto.setApproverIds(approvers);
        dto.setReferrer(referrers);
        return dto;
    }

    private ApiResponse<UserDetailResponse> mockUserResponse(Long id, String name) {
        return ApiResponse.success(
                UserDetailResponse.builder()
                        .user(UserDto.builder().id(id).name(name).build())
                        .build()
        );
    }

    @Nested
    @DisplayName("기안 작성 (Create)")
    class CreateDocumentTests {

        @Test
        @DisplayName("성공: 결재자 3명, 참조자 포함, 유저 검증 통과")
        void success() {
            // given
            String drafterId = "100";
            List<Long> approvers = List.of(200L, 300L, 400L);
            List<Long> referrers = List.of(500L);
            CreateDocumentRequestDto dto = createRequest(approvers, referrers);

            given(userFeignClient.findUserDetail(anyLong()))
                    .willReturn(mockUserResponse(999L, "테스트유저"));

            given(approvalDocumentRepository.save(any(ApprovalDocument.class)))
                    .willAnswer(invocation -> {
                        ApprovalDocument doc = invocation.getArgument(0);
                        ReflectionTestUtils.setField(doc, "id", 1L);
                        return doc;
                    });

            given(approvalDocumentRepository.existsById(anyLong())).willReturn(true);

            // when
            Long resultId = documentService.createDocument(drafterId, dto);

            // then
            assertThat(resultId).isEqualTo(1L);
            verify(approvalDocumentRepository).save(any()); // 문서 저장됨?
            verify(approvalLineRepository, times(3)).save(any()); // 결재선 3개 저장됨?
            verify(approvalReferrerRepository, times(1)).save(any()); // 참조자 1개 저장됨?
            verify(approvalHistoryRepositoy).save(any()); // 히스토리 저장됨?
        }

        @Test
        @DisplayName("실패: 결재자가 3명이 아님")
        void fail_invalid_count() {
            // given
            List<Long> approvers = List.of(200L, 300L); // 2명뿐
            CreateDocumentRequestDto dto = createRequest(approvers, null);

            // when & then
            assertThatThrownBy(() -> documentService.createDocument("100", dto))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_APPROVER_COUNT);
        }

        @Test
        @DisplayName("실패: 기안자가 결재선에 포함됨")
        void fail_self_approval() {
            // given
            Long drafterId = 100L;
            List<Long> approvers = List.of(100L, 200L, 300L); // 100번이 포함됨
            CreateDocumentRequestDto dto = createRequest(approvers, null);

            // when & then
            assertThatThrownBy(() -> documentService.createDocument(String.valueOf(drafterId), dto))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.DRAFTER_EQUALS_APPROVER);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저를 결재자로 지정")
        void fail_user_not_found() {
            // given
            List<Long> approvers = List.of(200L, 300L, 400L);
            CreateDocumentRequestDto dto = createRequest(approvers, null);

            lenient().when(userFeignClient.findUserDetail(anyLong())).thenReturn(null);

            lenient().when(approvalDocumentRepository.save(any(ApprovalDocument.class)))
                    .thenAnswer(invocation -> {
                        ApprovalDocument doc = invocation.getArgument(0);
                        ReflectionTestUtils.setField(doc, "id", 1L);
                        return doc;
                    });
            lenient().when(approvalDocumentRepository.existsById(anyLong())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> documentService.createDocument("100", dto))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("문서 상세 조회")
    class ReadDocumentTests {

        private ApprovalDocument createMockDocument(Long id, Long drafterId) {
            ApprovalDocument doc = ApprovalDocument.builder()
                    .drafter(drafterId)
                    .drafterName("기안자")
                    .title("제목")
                    .content("내용")
                    .docStatus(DocStatusEnum.IN_PROGRESS)
                    .createdAt(LocalDateTime.now())
                    .build();
            ReflectionTestUtils.setField(doc, "id", id);
            return doc;
        }

        @Test
        @DisplayName("성공: 기안자가 조회")
        void success_drafter() {
            // given
            Long docId = 1L;
            Long memberId = 100L; // 기안자
            ApprovalDocument document = createMockDocument(docId, memberId);

            given(approvalDocumentRepository.findById(docId)).willReturn(Optional.of(document));
            given(approvalLineRepository.findByDocumentOrderBySequence(docId)).willReturn(Collections.emptyList());
            given(approvalReferrerRepository.findByDocumentOrderByViewedAt(docId)).willReturn(Collections.emptyList());

            given(userFeignClient.findUserDetail(anyLong())).willReturn(mockUserResponse(memberId, "기안자"));

            // when
            DocumentDetailResponseDto result = documentService.readDetailDocument(String.valueOf(memberId), docId);

            // then
            assertThat(result.getTitle()).isEqualTo("제목");
            assertThat(result.getDrafterName()).isEqualTo("기안자");
        }

        @Test
        @DisplayName("성공: 결재자가 조회")
        void success_approver() {
            // given
            Long docId = 1L;
            Long drafterId = 100L;
            Long approverId = 200L;

            ApprovalDocument document = createMockDocument(docId, drafterId);

            ApprovalLine line = ApprovalLine.builder().approver(approverId).sequence(1).build();

            given(approvalDocumentRepository.findById(docId)).willReturn(Optional.of(document));
            given(approvalLineRepository.findByDocumentOrderBySequence(docId)).willReturn(List.of(line));
            given(approvalReferrerRepository.findByDocumentOrderByViewedAt(docId)).willReturn(Collections.emptyList());

            given(userFeignClient.findUserDetail(anyLong())).willReturn(mockUserResponse(999L, "유저"));

            // when
            DocumentDetailResponseDto result = documentService.readDetailDocument(String.valueOf(approverId), docId);

            // then
            assertThat(result.getDocumentId()).isEqualTo(docId);
        }

        @Test
        @DisplayName("성공: 참조자가 조회")
        void success_referrer() {
            // given
            Long docId = 1L;
            Long referrerId = 300L;

            ApprovalDocument document = createMockDocument(docId, 100L);
            ApprovalReferrer ref = ApprovalReferrer.builder().referrer(referrerId).build();

            given(approvalDocumentRepository.findById(docId)).willReturn(Optional.of(document));
            given(approvalLineRepository.findByDocumentOrderBySequence(docId)).willReturn(Collections.emptyList());
            given(approvalReferrerRepository.findByDocumentOrderByViewedAt(docId)).willReturn(List.of(ref));
            given(userFeignClient.findUserDetail(anyLong())).willReturn(mockUserResponse(999L, "유저"));

            // when
            DocumentDetailResponseDto result = documentService.readDetailDocument(String.valueOf(referrerId), docId);

            // then
            assertThat(result.getDocumentId()).isEqualTo(docId);
        }

        @Test
        @DisplayName("실패: 문서가 존재하지 않음")
        void fail_doc_not_found() {
            // given
            lenient().when(approvalDocumentRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> documentService.readDetailDocument("100", 999L))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 권한 없는 제3자가 접근")
        void fail_no_permission() {
            // given
            Long docId = 1L;
            Long strangerId = 999L; // 아무 관련 없는 사람

            ApprovalDocument document = createMockDocument(docId, 100L); // 기안자는 100번

            given(approvalDocumentRepository.findById(docId)).willReturn(Optional.of(document));

            given(approvalLineRepository.findByDocumentOrderBySequence(docId)).willReturn(List.of(
                    ApprovalLine.builder().approver(200L).build()
            ));
            given(approvalReferrerRepository.findByDocumentOrderByViewedAt(docId)).willReturn(Collections.emptyList());

            // when & then
            assertThatThrownBy(() -> documentService.readDetailDocument(String.valueOf(strangerId), docId))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.NO_READ_AUTHORIZATION);
        }
    }
}