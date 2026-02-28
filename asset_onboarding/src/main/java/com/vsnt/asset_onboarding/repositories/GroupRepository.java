package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    Page<Group> findAllByOrgId(String orgId, Pageable pageable);
}
