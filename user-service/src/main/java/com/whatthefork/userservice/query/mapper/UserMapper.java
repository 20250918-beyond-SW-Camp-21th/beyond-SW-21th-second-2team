package com.whatthefork.userservice.query.mapper;

import com.whatthefork.userservice.query.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserDTO> findAllUsers();

    UserDTO findUserById(Long userId);

    UserDTO findUserByEmail(String email);
}
