package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;

import java.util.Collection;

@RestController
@RequestMapping("/api/groups")
class GroupController {
  private final GroupFacade groupFacade;
  private final CurrentUserContext currentUserContext;

  GroupController(GroupFacade groupFacade, CurrentUserContext currentUserContext) {
    this.groupFacade = groupFacade;
    this.currentUserContext = currentUserContext;
  }

  @GetMapping("/{id}")
  ResponseEntity<GroupDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(groupFacade.getById(id));
  }

  @GetMapping("/user/{userId}")
  ResponseEntity<Collection<GroupDto>> findAllGroupsByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(groupFacade.findAllGroupsByUserId(userId));
  }

  @GetMapping("/{groupId}/members")
  ResponseEntity<Collection<MemberDto>> findAllMembersByGroupId(@PathVariable Long groupId) {
    return ResponseEntity.ok(groupFacade.findAllMembersByGroupId(groupId));
  }

  @PostMapping
  ResponseEntity<GroupDto> create(@ModelAttribute CreateGroupDto dto) {
    var user = currentUserContext.getSignedInUser();
    return ResponseEntity.ok(groupFacade.create(dto, user));
  }
}
