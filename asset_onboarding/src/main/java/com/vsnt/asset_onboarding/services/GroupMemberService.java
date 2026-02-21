package com.vsnt.asset_onboarding.services;

import com.vsnt.asset_onboarding.dtos.media.request.GroupMemberCreateRequestDTO;
import com.vsnt.asset_onboarding.entities.Group;
import com.vsnt.asset_onboarding.entities.GroupMember;
import com.vsnt.asset_onboarding.exceptions.EntityNotFoundException;
import com.vsnt.asset_onboarding.repositories.GroupMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupMemberService {
private final GroupMemberRepository groupMemberRepository;
private final GroupService groupService;
    public GroupMemberService(GroupMemberRepository groupMemberRepository, GroupService groupService) {
        this.groupMemberRepository = groupMemberRepository;
        this.groupService = groupService;
    }
    public GroupMember createGroupMember(GroupMemberCreateRequestDTO request){
        Group group = groupService.getGroup(UUID.fromString(request.getGroupId()));
        if(group==null){
            throw new EntityNotFoundException(
                    "Group"
            );
        }
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(group.getId());
        groupMember.setActive(true);
        groupMember.setUserId(request.getMemberUserId());

        return groupMemberRepository.save(groupMember);
    }
    public GroupMember getMember(UUID memberId){
        return groupMemberRepository.findById(memberId).orElse(null);
    }

    public void removeGroupMember(UUID id){
        GroupMember groupMember = getMember(id);
        if(groupMember == null)
        {
            throw new EntityNotFoundException("GroupMember");
        }
        groupMember.setActive(false)
        ;
        groupMemberRepository.save(groupMember);

    }
    public Page<GroupMember> getMembers(
            Specification<GroupMember> spec, int page , int limit
    )
    {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        return groupMemberRepository.findAll(spec, pageRequest);
    }
    public  Page<GroupMember> getMembers(UUID groupId, int page, int limit){
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        return groupMemberRepository.findAllByGroupId(groupId, pageRequest);
    }
    public boolean isGroupMember(String userId , Group group)
    {
        return groupMemberRepository.findByGroupIdAndUserId(group.getId(), userId).isPresent();
    }
}
