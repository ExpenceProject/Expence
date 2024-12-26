package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.InvitationStatus;
import ug.edu.pl.server.domain.group.dto.InvitationDto;

import java.util.Collection;

@RestController
@RequestMapping("/api/invitations")
class InvitationController {
    private final GroupFacade groupFacade;

    InvitationController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @GetMapping("/{invitationId}")
    ResponseEntity<InvitationDto> getInvitationById(@PathVariable("invitationId") Long invitationId) {
        return ResponseEntity.ok(groupFacade.getInvitationById(invitationId));
    }

    @GetMapping("/group/{groupId}/invitees/{inviteeId}")
    ResponseEntity<InvitationDto> getGroupInvitationForUser(@PathVariable("groupId") Long groupId, @PathVariable("inviteeId") Long inviteeId) {
        return ResponseEntity.ok(groupFacade.getInvitationByGroupAndInviteeId(groupId, inviteeId));
    }

    @GetMapping("/group/{groupId}")
    ResponseEntity<Collection<InvitationDto>> getGroupInvitations(@PathVariable("groupId") Long groupId) {
        return ResponseEntity.ok(groupFacade.getInvitationsByGroupId(groupId));
    }

    @GetMapping("/invitees/{inviteeId}")
    ResponseEntity<Collection<InvitationDto>> getUserInvitations(@PathVariable("inviteeId") Long inviteeId) {
        return ResponseEntity.ok(groupFacade.getInvitationsByInviteeId(inviteeId));
    }
    @PatchMapping("/{invitationId}")
    ResponseEntity<Void> updateInvitationStatus(@PathVariable("invitationId") Long invitationId, @RequestBody InvitationStatus invitationStatus) {
        groupFacade.updateInvitationStatus(invitationId, invitationStatus);
        return ResponseEntity.noContent().build();
    }
}
