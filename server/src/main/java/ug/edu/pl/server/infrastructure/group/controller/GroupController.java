package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;
import ug.edu.pl.server.domain.group.dto.UpdateGroupDto;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;

import java.util.Collection;

@RestController
@RequestMapping("/api/groups")
class GroupController {
  private final GroupFacade groupFacade;
  private final UserFacade userFacade;
  private final CurrentUserContext currentUserContext;

  GroupController(GroupFacade groupFacade,
                  UserFacade userFacade,
                  CurrentUserContext currentUserContext) {
    this.groupFacade = groupFacade;
    this.userFacade = userFacade;
    this.currentUserContext = currentUserContext;
  }

  @GetMapping("/{id}")
  ResponseEntity<GroupDto> getById(@PathVariable String id) {
    return ResponseEntity.ok(groupFacade.getById(id));
  }

  @GetMapping("/user/{userId}")
  ResponseEntity<Collection<GroupDto>> findAllGroupsByUserId(@PathVariable String userId) {
    return ResponseEntity.ok(groupFacade.findAllGroupsByUserId(userId));
  }

  @GetMapping("/{groupId}/members")
  ResponseEntity<Collection<MemberDto>> findAllMembersByGroupId(@PathVariable String groupId) {
    return ResponseEntity.ok(groupFacade.findAllMembersByGroupId(groupId));
  }

  @PostMapping
  ResponseEntity<GroupDto> create(@ModelAttribute CreateGroupDto dto) {
    var user = currentUserContext.getSignedInUser();
    return ResponseEntity.ok(groupFacade.create(dto, user));
  }

  @PutMapping("/{id}")
  ResponseEntity<GroupDto> updateGroup(@PathVariable String id, @ModelAttribute UpdateGroupDto dto) {
      return ResponseEntity.ok(groupFacade.updateGroup(id, dto));
  }

  @PatchMapping("/{groupId}/members/{memberId}/role")
  ResponseEntity<MemberDto> updateMemberRole(@PathVariable String groupId, @PathVariable String memberId, @RequestBody String role) {
      return ResponseEntity.ok(groupFacade.updateMemberRole(groupId, memberId, role));
  }

  @PatchMapping("/{groupId}/members/{memberId}/nickname")
  ResponseEntity<MemberDto> updateMemberNickname(@PathVariable String groupId, @PathVariable String memberId, @RequestBody String nickname) {
      return ResponseEntity.ok(groupFacade.updateMemberNickname(groupId, memberId, nickname));
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteGroup(@PathVariable String id) {
      groupFacade.deleteGroup(id);
      return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{groupId}/members/{memberId}")
  ResponseEntity<Void> deleteMember(@PathVariable String groupId, @PathVariable String memberId) {
    groupFacade.deleteMember(groupId, memberId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{groupId}/members/{memberId}/user")
  ResponseEntity<UserDto> getUserByMemberIdAndGroupId(@PathVariable String memberId, @PathVariable String groupId) {
    String userId = groupFacade.getUserIdByMemberIdAndGroupId(memberId, groupId);
    return ResponseEntity.ok(userFacade.getById(Long.valueOf(userId)));
  }
}
