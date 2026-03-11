package com.vsnt.user.services;

import com.vsnt.user.entities.Group;
import com.vsnt.user.entities.GroupMember;
import com.vsnt.user.entities.Organisation;
import com.vsnt.user.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberService groupMemberService;
    public GroupService(GroupRepository groupRepository, GroupMemberService groupMemberService) {
        this.groupRepository = groupRepository;
        this.groupMemberService = groupMemberService;
    }

    public Group getGroup(UUID id) {
        return groupRepository.findById(id).orElse(null);
    }
    public Group createGroup(String name , Organisation org)
    {
        Group group = new Group();
        group.setOrganisation(org);
        group.setName(name);
        group.setActive(true);
        groupRepository.save(group);
        return group;
    }
    public void closeGroup(UUID id) {
        Group group = getGroup(id);
        if(group == null)
        {
            throw new RuntimeException("Group not found");
        }
        group.setActive(false);
        groupRepository.save(group);
    }
    public List<Group> findGroups(String userId)
    {
        return groupMemberService.getMembers(userId).stream().map(
                GroupMember::getGroup
        ).toList();
    }

}
