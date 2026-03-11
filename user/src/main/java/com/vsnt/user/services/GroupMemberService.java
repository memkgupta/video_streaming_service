package com.vsnt.user.services;

import com.vsnt.user.entities.Group;
import com.vsnt.user.entities.GroupMember;
import com.vsnt.user.repositories.GroupMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    public GroupMemberService(GroupMemberRepository groupMemberRepository) {
        this.groupMemberRepository = groupMemberRepository;
    }

    public GroupMember createMember(String userId , Group group)
    {
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUserId(userId);
        member.setActive(true);
        return groupMemberRepository.save(member);
    }
    public GroupMember getGroupMember(UUID id)
    {
        return groupMemberRepository.findById(id).orElse(null);
    }

    public Page<GroupMember> getMembers(Specification<GroupMember> specification , Pageable pageable)
    {
        return groupMemberRepository.findAll(specification,pageable);
    }
    public List<GroupMember> getMembers(String userId)
    {
        return groupMemberRepository.findByUserId(userId);
    }
    public List<GroupMember> getMembers(UUID group_id)
    {
        return groupMemberRepository.findByGroup_Id(group_id);
    }

}
