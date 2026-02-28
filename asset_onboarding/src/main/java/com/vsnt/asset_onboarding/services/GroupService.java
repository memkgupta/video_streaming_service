package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.entities.Group;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.repositories.GroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
public class GroupService {
private final GroupRepository groupRepository;
public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
}
public Group getGroup(UUID id)
{
    return groupRepository.findById(id).orElse(null);
}
public Group createGroup(GroupCreateRequestDTO request)
{
    Group group = new Group();
    group.setName(request.getGroupName());
    group.setCreatedAt(Timestamp.from(Instant.now()));
    group.setUpdatedAt(Timestamp.from(Instant.now()));
    group.setOrgId(request.getOrgId());
    return groupRepository.save(group);
}
public Page<Group> getGroups(String orgId , int page , int size)
{
    PageRequest pageRequest = PageRequest.of(page-1, size);
    return groupRepository.findAllByOrgId(orgId, pageRequest);
}
public void deleteGroup(UUID id)
{
    Group grp = getGroup(id);
    if(grp == null)
    {
        throw new EntityNotFoundException("Group");
    }
    grp.setActive(false);
    groupRepository.save(grp);
}

}
