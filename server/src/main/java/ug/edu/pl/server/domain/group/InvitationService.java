package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.exception.ForbiddenException;
import ug.edu.pl.server.domain.group.dto.CreateInvitationsDto;
import ug.edu.pl.server.domain.group.dto.InvitationDto;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class InvitationService {
    private final InvitationRepository invitationRepository;
    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final MemberRepository memberRepository;

    InvitationService(InvitationRepository invitationRepository,
                        GroupRepository groupRepository,
                        GroupRoleRepository groupRoleRepository,
                      MemberRepository memberRepository) {
        this.invitationRepository = invitationRepository;
        this.groupRepository = groupRepository;
        this.groupRoleRepository = groupRoleRepository;
        this.memberRepository = memberRepository;
    }

    List<InvitationDto> create(CreateInvitationsDto invitationsDto) {
        Group group = groupRepository.findByIdOrThrow(Long.valueOf(invitationsDto.groupId()));

        Member inviter = memberRepository.findByUserIdAndGroupIdOrThrow(Long.valueOf(invitationsDto.inviterId()), group.getId());
        if (inviter.getGroupRole().getName() != GroupRoleName.ROLE_OWNER) {
            throw new ForbiddenException("You are not allowed to invite to this group.");
        }

        return invitationsDto.inviteeIds().stream().map(inviteeId -> {
            Invitation invitation = new Invitation();
            invitation.setInviteeId(Long.valueOf(inviteeId));
            invitation.setInviter(inviter);
            invitation.setGroup(group);
            invitation.setStatus(InvitationStatus.SENT);
            var savedInvitation = invitationRepository.saveOrThrow(invitation);
            return savedInvitation.dto();
        }).collect(Collectors.toList());
    }

    InvitationDto getById(String id) {
        return invitationRepository.findByIdOrThrow(Long.valueOf(id)).dto();
    }

    InvitationDto getByGroupAndInviteeId(String groupId, String inviteeId) {
        return invitationRepository.findByGroupAndInviteeIdOrThrow(Long.valueOf(groupId), Long.valueOf(inviteeId)).dto();
    }

    Collection<InvitationDto> getByInviteeId(String id, InvitationStatus status) {
        return invitationRepository.findInvitationsByInviteeIdFilterByStatus(Long.valueOf(id), status).stream().map(Invitation::dto).collect(Collectors.toList());
    }

    Collection<InvitationDto> getByGroupId(String id, InvitationStatus status) {
        return invitationRepository.findInvitationsByGroupIdFilterByStatus(Long.valueOf(id), status).stream().map(Invitation::dto).collect(Collectors.toList());
    }

    void updateInvitationStatus(String id, InvitationStatus invitationStatus, UserDto currentUser) {
        var invitation = invitationRepository.findByIdOrThrow(Long.valueOf(id));
        var group = invitation.getGroup();

        if (invitationStatus != InvitationStatus.CANCELLED
                && !Objects.equals(invitation.getInviteeId(), Long.valueOf(currentUser.id()))) {
            throw new ForbiddenException("You are not allowed to update this invitation.");
        }

        if (invitationStatus == InvitationStatus.ACCEPTED) {
            var member = new Member();
            member.setUserId(Long.valueOf(currentUser.id()));
            member.setNickname(currentUser.firstName());
            member.setGroupRole(groupRoleRepository.findByNameOrThrow(GroupRoleName.ROLE_MEMBER));
            member.setGroup(group);

            group.getMembers().add(member);
            groupRepository.saveOrThrow(group);
        }
        invitationRepository.updateStatusByIdOrThrow(Long.valueOf(id), invitationStatus);
    }
}
