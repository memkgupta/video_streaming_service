package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.LiveStream;
import com.vsnt.videos_service.entities.LiveStreamSegment;
import com.vsnt.videos_service.entities.SegmentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Stream;

public interface LiveStreamSegmentRepository extends JpaRepository<LiveStreamSegment, SegmentId> , JpaSpecificationExecutor<LiveStreamSegment> {
public long countByLiveStreamId(String liveStream);
    Page<LiveStreamSegment> findByLiveStreamIdOrderById_NumberAsc(
            String streamKey,
            Pageable pageable
    );
}
