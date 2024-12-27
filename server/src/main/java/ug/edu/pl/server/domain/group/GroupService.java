package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class GroupService {
  private final GroupRoleRepository groupRoleRepository;
  private final GroupRepository groupRepository;
  private final StorageFacade storageFacade;
  private final GroupCreator groupCreator;
  private final UserFacade userFacade;

  GroupService(
      GroupRepository groupRepository,
      GroupRoleRepository groupRoleRepository,
      StorageFacade storageFacade,
      GroupCreator groupCreator,
      UserFacade userFacade) {
    this.groupRepository = groupRepository;
    this.groupRoleRepository = groupRoleRepository;
    this.storageFacade = storageFacade;
    this.groupCreator = groupCreator;
    this.userFacade = userFacade;
  }

  GroupDto getById(Long id) {
    return groupRepository.findByIdOrThrow(id).dto();
  }

  GroupDto createGroup(CreateGroupDto dto, UserDto currentUser) {
    var group = createGroupEntity(dto);
    assignOwnerToGroup(group, currentUser);
    addInvitationsToGroup(dto, group, currentUser);
    return groupRepository.saveOrThrow(group).dto();
  }

  Collection<GroupDto> findAllGroupsByUserId(Long userId) {
    return groupRepository.findAllGroupsByUserId(userId).stream().map(Group::dto).collect(Collectors.toList());
  }

  Collection<MemberDto> findAllMembersByGroupId(Long groupId) {
    return groupRepository.findByIdOrThrow(groupId).getMembers().stream().map(Member::dto).collect(Collectors.toList());
  }

  private Group createGroupEntity(CreateGroupDto dto) {
    var group = groupCreator.from(dto);
    if (dto.file() != null && !dto.file().isEmpty()) {
      var imageKey = storageFacade.upload(dto.file());
      group.setImage(new Image(imageKey));
    }
    return group;
  }

  private void assignOwnerToGroup(Group group, UserDto currentUser) {
    var member = new Member();
    member.setUserId(currentUser.id());
    member.setNickname(currentUser.firstName());
    member.setGroupRole(groupRoleRepository.findByNameOrThrow(GroupRoleName.ROLE_OWNER));
    member.setGroup(group);
    group.getMembers().add(member);
  }

  private void addInvitationsToGroup(CreateGroupDto dto, Group group, UserDto currentUser) {
    if (dto.inviteesId() == null || dto.inviteesId().isEmpty()) return;

    var inviter =
        group.getMembers().stream()
            .filter(m -> m.getUserId().equals(currentUser.id()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(Group.class.getName(), currentUser.id()));

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
