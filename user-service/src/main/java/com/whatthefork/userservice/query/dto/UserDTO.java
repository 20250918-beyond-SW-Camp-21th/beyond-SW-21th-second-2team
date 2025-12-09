package com.whatthefork.userservice.query.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String dept_id;
    private boolean isAdmin;
    private boolean isDeptLeader;
    private int position_code;
}