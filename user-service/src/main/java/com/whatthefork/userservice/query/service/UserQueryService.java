package com.whatthefork.userservice.query.service;

import com.whatthefork.userservice.exception.UserNotFoundException;
import com.whatthefork.userservice.query.dto.UserDTO;
import com.whatthefork.userservice.query.dto.UserDetailResponse;
import com.whatthefork.userservice.query.dto.UserListResponse;
import com.whatthefork.userservice.query.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;

    public UserDetailResponse getUserDetail(Long userId) {
        UserDTO user = Optional.ofNullable(
                userMapper.findUserById(userId)
        ).orElseThrow(() -> new UserNotFoundException("유저 정보를 찾을 수 없습니다: ID = " + userId));

        return UserDetailResponse.builder().user(user).build();
    }

    public UserDetailResponse getUserById(Long userId) {
        UserDTO user = Optional.ofNullable(
                userMapper.findUserById(userId)
        ).orElseThrow(() -> new UserNotFoundException("유저 정보를 찾을 수 없습니다: ID = " + userId));

        return UserDetailResponse.builder().user(user).build();
    }

    public UserListResponse getAllUsers() {
        List<UserDTO> users = userMapper.findAllUsers();
        return UserListResponse.builder()
                .users(users)
                .build();
    }

//    public String getUserGrade(Long userId) {
//        UserDTO user = userMapper.findUserById(userId);
//        if (user == null) {
//            throw new RuntimeException("유저 정보 찾지 못함");
//        }
//
//        return "PREMIUM";
//    }
}
