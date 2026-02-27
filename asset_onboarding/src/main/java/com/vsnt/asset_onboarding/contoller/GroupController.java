package com.vsnt.asset_onboarding.contoller;

import com.vsnt.asset_onboarding.dtos.PageResponseDTO;
import com.vsnt.asset_onboarding.dtos.media.request.GroupCreateRequestDTO;
import com.vsnt.asset_onboarding.dtos.media.response.GroupDTO;
import com.vsnt.asset_onboarding.entities.Group;
import com.vsnt.asset_onboarding.mapper.GroupMapper;
import com.vsnt.asset_onboarding.services.GroupService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/group")
public class GroupController {
    private final GroupService groupService;
    private final GroupMapper groupMapper;

    public GroupController(GroupService groupService, GroupMapper groupMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
    }

    @PostMapping
    public ResponseEntity<GroupDTO>
    createGroup(@RequestBody GroupCreateRequestDTO request){
        Group grp = groupService.createGroup(request);
        GroupDTO dto = groupMapper.toGroupDTO(grp);
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public ResponseEntity<PageResponseDTO<GroupDTO>>
        getAllGroups(@RequestParam String orgId , @RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size){
                    Page<Group> resPage = groupService.getGroups(
                            orgId, page, size
                    );
                    List<GroupDTO> content =
                       resPage.getContent().stream().map(
                               g->groupMapper.toGroupDTO(g)
                       ).toList();
                    PageResponseDTO<GroupDTO> res =
                            PageResponseDTO.<GroupDTO>builder()
                                    .page(page)
                                    .total(resPage.getTotalElements())
                                    .data(content)
                                    .hasNext(resPage.hasNext())
                                    .hasPrevious(resPage.hasPrevious())
                                    .build();
                    return ResponseEntity.ok(res);

    }
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID groupId){
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable UUID groupId){
        Group grp = groupService.getGroup(groupId);
        GroupDTO dto = groupMapper.toGroupDTO(grp);
        return ResponseEntity.ok(dto);
    }

}
