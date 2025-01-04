package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.InvitationStatus;
import ug.edu.pl.server.domain.group.dto.CreateInvitationsDto;
import ug.edu.pl.server.domain.group.dto.InvitationDto;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;

import java.util.Collection;

@RestController
@RequestMapping("/api/invitations")
class InvitationController {
    private final GroupFacade groupFacade;
    private final CurrentUserContext currentUserContext;

    InvitationController(GroupFacade groupFacade, CurrentUserContext currentUserContext) {
        this.groupFacade = groupFacade;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping("/{invitationId}")
    ResponseEntity<InvitationDto> getInvitationById(@PathVariable("invitationId") String invitationId) {
        return ResponseEntity.ok(groupFacade.getInvitationById(invitationId));
    }

    @GetMapping("/group/{groupId}/invitees/{inviteeId}")
    ResponseEntity<InvitationDto> getGroupInvitationForUser(@PathVariable("groupId") String groupId, @PathVariable("inviteeId") String inviteeId) {
        return ResponseEntity.ok(groupFacade.getInvitationByGroupAndInviteeId(groupId, inviteeId));
    }

    @GetMapping("/group/{groupId}")
    ResponseEntity<Collection<InvitationDto>> getGroupInvitations(@PathVariable("groupId") String groupId, @RequestParam(value = "status", required = false) InvitationStatus status) {
        if (status == null) {
            return ResponseEntity.ok(groupFacade.getInvitationsByGroupId(groupId, null));
        } else {
            return ResponseEntity.ok(groupFacade.getInvitationsByGroupId(groupId, status));
        }
    }

    @GetMapping("/invitees/{inviteeId}")
    ResponseEntity<Collection<InvitationDto>> getUserInvitations(@PathVariable("inviteeId") String inviteeId, @RequestParam(value = "status", required = false) InvitationStatus status) {
        if (status == null) {
            return ResponseEntity.ok(groupFacade.getInvitationsByInviteeId(inviteeId, null));
        } else {
            return ResponseEntity.ok(groupFacade.getInvitationsByInviteeId(inviteeId, status));
        }
    }

    @PostMapping
    ResponseEntity<Collection<InvitationDto>> createInvitations(@ModelAttribute CreateInvitationsDto createInvitationsDto) {
        return ResponseEntity.ok(groupFacade.createInvitations(createInvitationsDto));
    }

    @PatchMapping("/{invitationId}")
    ResponseEntity<Void> updateInvitationStatus(@PathVariable("invitationId") String invitationId, @RequestBody InvitationStatus invitationStatus) {
        var user = currentUserContext.getSignedInUser();
        groupFacade.updateInvitationStatus(invitationId, invitationStatus, user);
        return ResponseEntity.noContent().build();
    }
}
