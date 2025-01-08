package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.*;
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

  @PostMapping
  ResponseEntity<GroupDto> create(@ModelAttribute CreateGroupDto dto) {
    var user = currentUserContext.getSignedInUser();
    return ResponseEntity.ok(groupFacade.create(dto, user));
  }

  @PutMapping("/{id}")
  ResponseEntity<GroupDto> updateGroup(@PathVariable String id, @ModelAttribute UpdateGroupDto dto) {
    var user = currentUserContext.getSignedInUser();
    return ResponseEntity.ok(groupFacade.updateGroup(id, dto, user.id()));
  }

  @PatchMapping("/{id}/settledDown")
  ResponseEntity<GroupDto> updateGroupSettledDown(@PathVariable String id) {
    var user = currentUserContext.getSignedInUser();
    return ResponseEntity.ok(groupFacade.updateGroupSettledDown(id, user.id()));
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteGroup(@PathVariable String id) {
      var user = currentUserContext.getSignedInUser();
      groupFacade.deleteGroup(id, user.id());
      return ResponseEntity.noContent().build();
  }

  @GetMapping("/members/{memberId}/balance")
  ResponseEntity<Collection<MemberBalanceDto>> getMemberBalance(@PathVariable Long memberId){
    return ResponseEntity.ok(groupFacade.getMemberBalance(memberId));
  }

  @GetMapping("/{groupId}/members")
  ResponseEntity<Collection<MemberDto>> findAllMembersByGroupId(@PathVariable String groupId) {
    return ResponseEntity.ok(groupFacade.findAllMembersByGroupId(groupId));
  }

  @PatchMapping("/{groupId}/members/{memberId}/role")
  ResponseEntity<MemberDto> updateMemberRole(@PathVariable String groupId, @PathVariable String memberId, @RequestBody String role) {
    var user = currentUserContext.getSignedInUser();
    return ResponseEntity.ok(groupFacade.updateMemberRole(groupId, memberId, role, user.id()));
  }

  @PatchMapping("/{groupId}/members/{memberId}/nickname")
  ResponseEntity<MemberDto> updateMemberNickname(@PathVariable String groupId, @PathVariable String memberId, @RequestBody String nickname) {
    return ResponseEntity.ok(groupFacade.updateMemberNickname(groupId, memberId, nickname));
  }

  @DeleteMapping("/{groupId}/members/{memberId}")
  ResponseEntity<Void> deleteMember(@PathVariable String groupId, @PathVariable String memberId) {
    var user = currentUserContext.getSignedInUser();
    groupFacade.deleteMember(groupId, memberId, user.id());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{groupId}/members/{memberId}/user")
  ResponseEntity<UserDto> getUserByMemberIdAndGroupId(@PathVariable String memberId, @PathVariable String groupId) {
    String userId = groupFacade.getUserIdByMemberIdAndGroupId(memberId, groupId);
    return ResponseEntity.ok(userFacade.getById(Long.valueOf(userId)));
  }
}
