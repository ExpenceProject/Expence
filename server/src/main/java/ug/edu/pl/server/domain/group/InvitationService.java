package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.group.dto.InvitationDto;

import java.util.Collection;
import java.util.stream.Collectors;

class InvitationService {
    private final InvitationRepository invitationRepository;

    InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    InvitationDto getById(Long id) {
        return invitationRepository.findByIdOrThrow(id).dto();
    }

    InvitationDto getByGroupAndInviteeId(Long groupId, Long inviteeId) {
        return invitationRepository.findByGroupAndInviteeIdOrThrow(groupId, inviteeId).dto();
    }

    Collection<InvitationDto> getByInviteeId(Long id) {
        return invitationRepository.findInvitationsByInviteeId(id).stream().map(Invitation::dto).collect(Collectors.toList());
    }

    Collection<InvitationDto> getByGroupId(Long id) {
        return invitationRepository.findInvitationsByGroupId(id).stream().map(Invitation::dto).collect(Collectors.toList());
    }

    void updateInvitationStatus(Long id, InvitationStatus invitationStatus) {
        invitationRepository.updateStatusByIdOrThrow(id, invitationStatus);
    }
}
