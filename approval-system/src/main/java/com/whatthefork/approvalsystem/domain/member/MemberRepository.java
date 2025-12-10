package com.whatthefork.approvalsystem.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    String findByName(String name);

    long countAllByIdIn(List<Long> approvalIds);
}
