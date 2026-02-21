package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.TranscodedSegment;
import com.vsnt.asset_onboarding.entities.TranscodedSegmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TranscodedSegmentRepository extends JpaRepository<
        TranscodedSegment , TranscodedSegmentId> , JpaSpecificationExecutor<TranscodedSegment> {
}
