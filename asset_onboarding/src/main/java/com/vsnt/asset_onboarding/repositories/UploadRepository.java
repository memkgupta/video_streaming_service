package com.vsnt.asset_onboarding.repositories;

import com.vsnt.asset_onboarding.entities.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadRepository extends JpaRepository<Upload, Long> {
    public List<Upload> findAllByUserId(String userId);
    public Upload findById(long id);
}
