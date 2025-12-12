package com.whatthefork.communicationandalarm.common.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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