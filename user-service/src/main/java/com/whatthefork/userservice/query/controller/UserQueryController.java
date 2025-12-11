package com.whatthefork.userservice.query.controller;

import com.whatthefork.userservice.common.ApiResponse;
import com.whatthefork.userservice.query.dto.UserDetailResponse;
import com.whatthefork.userservice.query.dto.UserListResponse;
import com.whatthefork.userservice.query.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Query", description = "정보 조회 관련 API)")
@RestController
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;


    @Operation(summary = "마이 페이지 조회", description = "로그인한 직원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비밀번호를 제외한 로그이한 직원의 정보 조회"
            )
    })
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
            @AuthenticationPrincipal String userId
    ) {
        UserDetailResponse response = userQueryService.getUserDetail(Long.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "서버내부용 직원 정보 조회", description = "요청된 직원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "URL에 포함된 직원번호를 가진 직원의 정보를 조회"
            )
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> findUserDetail(
            @PathVariable Long userId
    ) {
        UserDetailResponse response = userQueryService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "전체 직원 목록 조회", description = "등록된 모든 직원들의 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "모든 직원들의 정보 조회"
            )
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<UserListResponse>> getUsers() {
        UserListResponse response = userQueryService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @Operation(summary = "특정 직원 정보 조회", description = "관리자가 원하는 직원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "입력된 직원번호의 직원의 정보 조회"
            )
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable Long userId) {
        UserDetailResponse response = userQueryService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
