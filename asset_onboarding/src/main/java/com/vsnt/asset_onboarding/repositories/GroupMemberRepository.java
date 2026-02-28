package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID>, JpaSpecificationExecutor<GroupMember> {
    Page<GroupMember> findAllByGroupId(UUID groupId,Pageable pageable);
    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, String userId);
}
