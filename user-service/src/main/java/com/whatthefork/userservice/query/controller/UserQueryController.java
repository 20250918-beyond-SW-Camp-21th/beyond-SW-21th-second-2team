package com.whatthefork.userservice.query.controller;

import com.whatthefork.userservice.common.ApiResponse;
import com.whatthefork.userservice.query.dto.UserDetailResponse;
import com.whatthefork.userservice.query.dto.UserListResponse;
import com.whatthefork.userservice.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
            @AuthenticationPrincipal String userId
    ) {
        UserDetailResponse response = userQueryService.getUserDetail(Long.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> findUserDetail(
            @PathVariable Long userId
    ) {
        UserDetailResponse response = userQueryService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<UserListResponse>> getUsers() {
        UserListResponse response = userQueryService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable Long userId) {
        UserDetailResponse response = userQueryService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
