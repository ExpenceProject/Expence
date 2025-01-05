package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;
import ug.edu.pl.server.domain.group.dto.UpdateGroupDto;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class GroupService {
  private final GroupRoleRepository groupRoleRepository;
  private final GroupRepository groupRepository;
  private final MemberRepository memberRepository;
  private final StorageFacade storageFacade;
  private final GroupCreator groupCreator;
  private final UserFacade userFacade;

  GroupService(
      GroupRepository groupRepository,
      GroupRoleRepository groupRoleRepository,
      MemberRepository memberRepository,
      StorageFacade storageFacade,
      GroupCreator groupCreator,
      UserFacade userFacade) {
    this.groupRepository = groupRepository;
    this.groupRoleRepository = groupRoleRepository;
    this.memberRepository = memberRepository;
    this.storageFacade = storageFacade;
    this.groupCreator = groupCreator;
    this.userFacade = userFacade;
  }

  GroupDto getById(String id) {
    return groupRepository.findByIdOrThrow(Long.valueOf(id)).dto();
  }

  GroupDto createGroup(CreateGroupDto dto, UserDto currentUser) {
    var group = createGroupEntity(dto);
    assignOwnerToGroup(group, currentUser);
    addInvitationsToGroup(dto, group, currentUser);
    return groupRepository.saveOrThrow(group).dto();
  }

  GroupDto updateGroup(String id, UpdateGroupDto dto) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(id));
    group.setName(dto.name());
    if (dto.file() != null && !dto.file().isEmpty()) {
      var imageKey = storageFacade.upload(dto.file());
      group.setImage(new Image(imageKey));
    }
    return groupRepository.saveOrThrow(group).dto();
  }

  void deleteGroup(String id) {
    groupRepository.deleteById(Long.valueOf(id));
  }

  void deleteMember(String groupId, String memberId) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(groupId));
    var member = group.getMembers().stream()
        .filter(m -> m.getId().equals(Long.valueOf(memberId)))
        .findFirst()
        .orElseThrow(() -> new NotFoundException(Member.class.getName(), Long.valueOf(memberId)));
    group.getMembers().remove(member);
    groupRepository.saveOrThrow(group);
  }

  String getUserIdFromMember(String memberId, String groupId) {
    return memberRepository.findUserIdByIdAndGroupId(memberId, groupId);
  }

  MemberDto updateMemberRole(String groupId, String memberId, String roleName) {
    var member = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(memberId), Long.valueOf(groupId));
    var role = groupRoleRepository.findByNameOrThrow(GroupRoleName.valueOf(roleName));

    member.setGroupRole(role);
    return memberRepository.saveOrThrow(member).dto();
  }

  MemberDto updateMemberNickname(String groupId, String memberId, String nickname) {
    var member = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(memberId), Long.valueOf(groupId));
    member.setNickname(nickname);
    return memberRepository.saveOrThrow(member).dto();
  }

  Collection<GroupDto> findAllGroupsByUserId(String userId) {
    return groupRepository.findAllGroupsByUserId(Long.valueOf(userId)).stream().map(Group::dto).collect(Collectors.toList());
  }

  Collection<MemberDto> findAllMembersByGroupId(String groupId) {
    return groupRepository.findByIdOrThrow(Long.valueOf(groupId)).getMembers().stream().map(Member::dto).collect(Collectors.toList());
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
    member.setUserId(Long.valueOf(currentUser.id()));
    member.setNickname(currentUser.firstName());
    member.setGroupRole(groupRoleRepository.findByNameOrThrow(GroupRoleName.ROLE_OWNER));
    member.setGroup(group);
    group.getMembers().add(member);
  }

  private void addInvitationsToGroup(CreateGroupDto dto, Group group, UserDto currentUser) {
    if (dto.inviteesId() == null || dto.inviteesId().isEmpty()) return;

    var inviter =
        group.getMembers().stream()
            .filter(m -> m.getUserId().equals(Long.valueOf(currentUser.id())))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(Group.class.getName(), Long.valueOf(currentUser.id())));

    Set<Invitation> invitations = new HashSet<>();
    for (String id : dto.inviteesId()) {
      if (userFacade.getById(Long.valueOf(id)) != null) {
        var invitation = new Invitation();
        invitation.setInviteeId(Long.valueOf(id));
        invitation.setInviter(inviter);
        invitation.setGroup(group);
        invitation.setStatus(InvitationStatus.SENT);
        invitations.add(invitation);
      }
    }
    group.setInvitations(invitations);
  }
}
