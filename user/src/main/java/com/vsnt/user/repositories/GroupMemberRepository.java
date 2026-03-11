package com.vsnt.user.repositories;

import com.vsnt.user.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> , JpaSpecificationExecutor<GroupMember> {
    List<GroupMember> findByGroup_Id(UUID group_Id);
    List<GroupMember> findByUserId(String user_Id);
}
