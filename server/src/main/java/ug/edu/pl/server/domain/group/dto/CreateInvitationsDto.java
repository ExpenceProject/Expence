package ug.edu.pl.server.domain.group.dto;

import java.util.Collection;

public record CreateInvitationsDto(Collection<Long> inviteeIds,
                                   Long inviterId,
                                   Long groupId) {}