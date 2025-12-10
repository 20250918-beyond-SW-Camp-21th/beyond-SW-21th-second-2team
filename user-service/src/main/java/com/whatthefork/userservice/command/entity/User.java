package com.whatthefork.userservice.command.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int positionCode;

    @Column(nullable = false)
    private String deptId;

    @Column(nullable = false)
    private boolean isDeptLeader;

    @Column(nullable = false)
    private boolean isAdmin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder
    public User(String name, String email, String password, int positionCode, String deptId, boolean isDeptLeader, boolean isAdmin, UserRole role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.positionCode = positionCode;
        this.deptId = deptId;
        this.isDeptLeader = isDeptLeader;
        this.isAdmin = isAdmin;
        this.role = role != null ? role : (isAdmin ? UserRole.ADMIN : UserRole.USER);
    }

    public User(Long id, String name, String email, String password, int positionCode, String deptId, boolean isDeptLeader, boolean isAdmin){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.positionCode = positionCode;
        this.deptId = deptId;
        this.isDeptLeader = isDeptLeader;
        this.isAdmin = isAdmin;
        this.role = isAdmin ? UserRole.ADMIN : UserRole.USER;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}