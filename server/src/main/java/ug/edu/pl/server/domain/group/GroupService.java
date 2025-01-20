package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.exception.ForbiddenException;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.group.dto.*;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.math.BigDecimal;
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

  GroupDto updateGroup(String id, UpdateGroupDto dto, String userId) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(id));
    verifyIfGroupIsNotSettledDown(group.getSettledDown());
    verifyUserIsTheOwnerOfGroup(group, userId);

    group.setName(dto.name());
    if (dto.file() != null && !dto.file().isEmpty()) {
      var imageKey = storageFacade.upload(dto.file());
      group.setImage(new Image(imageKey));
    }
    return groupRepository.saveOrThrow(group).dto();
  }

  GroupDto updateGroupSettledDown(String id, String userId) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(id));
    verifyUserIsTheOwnerOfGroup(group, userId);

    group.setSettledDown(!group.getSettledDown());
    return groupRepository.saveOrThrow(group).dto();
  }

  void verifyIfGroupIsNotSettledDown(String id) {
    var settledDown = groupRepository.findByIdOrThrow(Long.valueOf(id)).getSettledDown();
    verifyIfGroupIsNotSettledDown(settledDown);
  }

  private void verifyIfGroupIsNotSettledDown(boolean settledDown) {
    if (settledDown) {
      throw new ForbiddenException("Action denied: This group is settled down and no further actions are allowed.");
    }
  }

  void verifyUserIsTheOwnerOfGroup(String groupId, String userId) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(groupId));
    verifyUserIsTheOwnerOfGroup(group, userId);
  }

  void verifyUserIsTheOwnerOfGroup(Group group, String userId) {
    boolean isOwner = group.getMembers().stream()
            .anyMatch(member -> member.getUserId().equals(Long.valueOf(userId))
                    && member.getGroupRole().getName().equals(GroupRoleName.ROLE_OWNER));

    if (!isOwner) {
      throw new ForbiddenException("User is not the owner of the group");
    }
  }

  void deleteGroup(String id, String userId) {
    verifyUserIsTheOwnerOfGroup(id, userId);
    groupRepository.deleteById(Long.valueOf(id));
  }

  void deleteMember(String groupId, String memberId, String userId) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(groupId));
    verifyIfGroupIsNotSettledDown(group.getSettledDown());
    verifyUserIsTheOwnerOfGroup(group, userId);

    if (memberRepository.isMemberIncludedInGroupHistory(Long.valueOf(memberId), Long.valueOf(groupId))) {
      throw new ForbiddenException("You cannot remove member with associated expenses or payments!");
    }

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

  MemberDto updateMemberRole(String groupId, String memberId, String roleName, String userId) {
    var group = groupRepository.findByIdOrThrow(Long.valueOf(groupId));
    verifyIfGroupIsNotSettledDown(group.getSettledDown());
    verifyUserIsTheOwnerOfGroup(group, userId);

    var member = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(memberId), Long.valueOf(groupId));
    var role = groupRoleRepository.findByNameOrThrow(GroupRoleName.valueOf(roleName));

    member.setGroupRole(role);
    return memberRepository.saveOrThrow(member).dto();
  }

  MemberDto updateMemberNickname(String groupId, String memberId, String nickname) {
    verifyIfGroupIsNotSettledDown(groupId);
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

  Collection<MemberBalanceDto> getMemberBalance(Long memberId, Long groupId) {
    Collection<Object[]> results = memberRepository.findMemberBalanceOrThrow(memberId, groupId);

    return results.stream()
            .map(result -> new MemberBalanceDto(
                    String.valueOf(result[0]),
                    String.valueOf(result[1]),
                    (String) result[2],
                    ((BigDecimal) result[3])
            ))
            .toList();
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
