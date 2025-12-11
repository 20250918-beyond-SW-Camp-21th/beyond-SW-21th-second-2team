package com.whatthefork.approvalsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatthefork.approvalsystem.common.error.BusinessException;
import com.whatthefork.approvalsystem.common.error.ErrorCode;
import com.whatthefork.approvalsystem.dto.request.CreateDocumentRequestDto;
import com.whatthefork.approvalsystem.dto.response.DocumentDetailResponseDto;
import com.whatthefork.approvalsystem.service.DocumentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "1")
    @DisplayName("[POST] 기안 작성 성공")
    void createDocument_success() throws Exception {
        // given
        CreateDocumentRequestDto requestDto = new CreateDocumentRequestDto();
        requestDto.setTitle("휴가 신청서");
        requestDto.setContent("연차 사용합니다.");
        requestDto.setApproverIds(List.of(2L, 3L, 4L));
        requestDto.setStartVacationDate(LocalDate.now());
        requestDto.setEndVacationDate(LocalDate.now().plusDays(1));

        given(documentService.createDocument(any(), any(CreateDocumentRequestDto.class)))
                .willReturn(100L);

        // when & then
        mockMvc.perform(post("/document/drafting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(100L));
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("[POST] 기안 작성 실패 - 결재자 수 오류")
    void createDocument_fail_invalidApproverCount() throws Exception {
        // given
        CreateDocumentRequestDto requestDto = new CreateDocumentRequestDto();
        requestDto.setTitle("제목");
        requestDto.setContent("내용");

        requestDto.setApproverIds(List.of(2L, 3L, 4L));
        requestDto.setStartVacationDate(LocalDate.now());
        requestDto.setEndVacationDate(LocalDate.now().plusDays(1));

        given(documentService.createDocument(any(), any(CreateDocumentRequestDto.class)))
                .willThrow(new BusinessException(ErrorCode.INVALID_APPROVER_COUNT));

        // when & then
        mockMvc.perform(post("/document/drafting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("[POST] 기안 작성 실패 - 기안자와 결재자 동일")
    void createDocument_fail_drafterEqualsApprover() throws Exception {
        // given
        CreateDocumentRequestDto requestDto = new CreateDocumentRequestDto();
        requestDto.setTitle("제목");
        requestDto.setContent("내용");
        requestDto.setApproverIds(List.of(1L, 2L, 3L)); //

        requestDto.setStartVacationDate(LocalDate.now());
        requestDto.setEndVacationDate(LocalDate.now().plusDays(1));

        given(documentService.createDocument(any(), any(CreateDocumentRequestDto.class)))
                .willThrow(new BusinessException(ErrorCode.DRAFTER_EQUALS_APPROVER));

        // when & then
        mockMvc.perform(post("/document/drafting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("[POST] 기안 작성 실패 - 존재하지 않는 결재자")
    void createDocument_fail_memberNotFound() throws Exception {
        // given
        CreateDocumentRequestDto requestDto = new CreateDocumentRequestDto();
        requestDto.setTitle("제목");
        requestDto.setContent("내용");
        requestDto.setApproverIds(List.of(999L, 2L, 3L));

        requestDto.setStartVacationDate(LocalDate.now());
        requestDto.setEndVacationDate(LocalDate.now().plusDays(1));

        given(documentService.createDocument(any(), any(CreateDocumentRequestDto.class)))
                .willThrow(new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/document/drafting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("[GET] 상세 조회 성공 - 정상 조회 및 로그 기록 호출")
    void getDocumentDetail_success() throws Exception {
        // given
        Long docId = 100L;

        DocumentDetailResponseDto responseDto = DocumentDetailResponseDto.builder()
                .documentId(docId)
                .title("테스트 문서")
                .content("내용")
                .drafterName("홍길동")
                .build();

        given(documentService.readDetailDocument(any(), eq(docId))).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/document/{docId}", docId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.documentId").value(100L))
                .andExpect(jsonPath("$.data.title").value("테스트 문서"));

        verify(documentService).writeReadHistory(eq(docId), any());
    }

    @Test
    @WithMockUser(username = "2")
    @DisplayName("[GET] 상세 조회 실패 - 권한 없음")
    void getDocumentDetail_fail_noAuth() throws Exception {
        // given
        Long docId = 100L;

        doThrow(new BusinessException(ErrorCode.NO_READ_AUTHORIZATION))
                .when(documentService).writeReadHistory(eq(docId), any());

        // when & then
        mockMvc.perform(get("/document/{docId}", docId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("[GET] 상세 조회 실패 - 존재하지 않는 문서")
    void getDocumentDetail_fail_notFound() throws Exception {
        // given
        Long docId = 999L;

        doThrow(new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND))
                .when(documentService).writeReadHistory(eq(docId), any());

        // when & then
        mockMvc.perform(get("/document/{docId}", docId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}