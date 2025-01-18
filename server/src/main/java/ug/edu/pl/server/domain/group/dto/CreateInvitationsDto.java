package ug.edu.pl.server.domain.group.dto;

import java.util.Collection;

public record CreateInvitationsDto(Collection<String> inviteeIds,
                                   String inviterId,
                                   String groupId) {}