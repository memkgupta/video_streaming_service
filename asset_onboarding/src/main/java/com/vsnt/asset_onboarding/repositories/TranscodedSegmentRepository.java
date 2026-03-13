package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.TranscodedSegment;
import com.vsnt.asset_onboarding.entities.TranscodedSegmentId;
import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface TranscodedSegmentRepository extends JpaRepository<
        TranscodedSegment , TranscodedSegmentId> , JpaSpecificationExecutor<TranscodedSegment> {

    Stream<TranscodedSegment> findById_AssetIdAndId_ResolutionOrderById_SequenceNumber(String assetId , ResolutionEnum resolution);
    @Query("""
    select max (s.duration) from TranscodedSegment  s where 
    s.mediaId = :mediaId and s.id.resolution = :resolution
""")
    Optional<Long> getMaxDuration(@Param("mediaId") String mediaId , @Param("resolution")ResolutionEnum resolution);

}
