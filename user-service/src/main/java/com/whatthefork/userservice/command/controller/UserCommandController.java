package com.whatthefork.userservice.command.controller;

import com.whatthefork.userservice.command.dto.ChangePasswordRequest;
import com.whatthefork.userservice.command.dto.UserCreateRequest;
import com.whatthefork.userservice.command.service.UserCommandService;
import com.whatthefork.userservice.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Command", description = "유저 정보 수정 및 삭제 관련 API")
@RestController
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

    @Operation(summary = "직원 등록", description = "입력된 정보로 신규 직원을 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "직원이 등록됨"
            )
    })
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody UserCreateRequest request) {
        userCommandService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 신규 비밀번호로 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "신규 비밀번호로 변경하고 암호화"
            )
    })
    @PostMapping("/users/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userCommandService.changeOwnPassword(Long.valueOf(userId), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "직원 삭제", description = "직원 계정 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "직원의 계정 삭제"
            )
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userCommandService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
