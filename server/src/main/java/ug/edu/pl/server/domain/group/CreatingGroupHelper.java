package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.exception.UserNotFoundException;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;

import java.util.HashSet;
import java.util.Set;


class CreatingGroupHelper {
    private final GroupRoleRepository groupRoleRepository;
    private final StorageFacade storageFacade;
    private final GroupCreator groupCreator;
    private final CurrentUserContext currentUserContext;
    private final UserFacade userFacade;

    CreatingGroupHelper(GroupRoleRepository groupRoleRepository, StorageFacade storageFacade, GroupCreator groupCreator, CurrentUserContext currentUserContext, UserFacade userFacade) {
        this.groupRoleRepository = groupRoleRepository;
        this.storageFacade = storageFacade;
        this.groupCreator = groupCreator;
        this.currentUserContext = currentUserContext;
        this.userFacade = userFacade;
    }

    Group createGroup(CreateGroupDto dto) {
        var group = createGroupEntity(dto);
        assignOwnerToGroup(group);
        addInvitationsToGroup(dto, group);
        return group;
    }

    private Group createGroupEntity(CreateGroupDto dto) {
        var group = groupCreator.from(dto);
        if (dto.file() != null && !dto.file().isEmpty()) {
            var imageKey = storageFacade.upload(dto.file());
            group.setImage(new Image(imageKey));
        }
        return group;
    }

    private void assignOwnerToGroup(Group group) {
        var signedInUser = currentUserContext.getSignedInUser();
        var member = new Member();
        member.setUserId(signedInUser.id());
        member.setNickname(signedInUser.firstName());
        member.setGroupRole(groupRoleRepository.findByNameOrThrow(GroupRoleName.ROLE_OWNER));
        member.setGroup(group);
        group.getMembers().add(member);
    }

    private void addInvitationsToGroup(CreateGroupDto dto, Group group) {
        if (dto.inviteesId() == null || dto.inviteesId().isEmpty()) return;

        var signedInUser = currentUserContext.getSignedInUser();
        var inviter = group.getMembers()
                .stream()
                .filter(m -> m.getUserId().equals(signedInUser.id()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(signedInUser.id()));

        Set<Invitation> invitations = new HashSet<>();
        for (Long id : dto.inviteesId()) {
            if (userFacade.getById(id) != null) {
                var invitation = new Invitation();
                invitation.setInviteeId(id);
                invitation.setInviter(inviter);
                invitation.setGroup(group);
                invitation.setStatus(InvitationStatus.SENT);
                invitations.add(invitation);
            }
        }
        group.setInvitations(invitations);
    }

}
