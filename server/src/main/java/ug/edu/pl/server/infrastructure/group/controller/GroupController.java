package ug.edu.pl.server.infrastructure.group.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;

@RestController
@RequestMapping("/api/groups")
class GroupController {
    private final GroupFacade groupFacade;

    GroupController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @GetMapping("/{id}")
    ResponseEntity<GroupDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupFacade.getById(id));
    }

    @PostMapping
    ResponseEntity<GroupDto> create(@Valid @ModelAttribute CreateGroupDto dto) {
        return ResponseEntity.ok(groupFacade.create(dto));
    }


}
