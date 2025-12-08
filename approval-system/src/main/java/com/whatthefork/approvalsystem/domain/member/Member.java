package com.whatthefork.approvalsystem.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

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

    @Builder
    public Member(String name, String email, String password, int positionCode, String deptId, boolean isDeptLeader, boolean isAdmin){
        this.name = name;
        this.email = email;
        this.password = password;
        this.positionCode = positionCode;
        this.deptId = deptId;
        this.isDeptLeader = isDeptLeader;
        this.isAdmin = isAdmin;
    }
}
