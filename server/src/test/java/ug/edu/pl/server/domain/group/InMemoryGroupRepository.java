package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.HashSet;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryGroupRepository implements GroupRepository, InMemoryRepository<Group> {
  static final Map<Long, Group> groupIdMap = new ConcurrentHashMap<>();

  @Override
  public Group save(Group group) {
    updateTimestampsAndVersion(group);
    groupIdMap.put(group.getId(), group);
    Optional<Member> owner = group.getMembers().stream().findFirst();
    owner.ifPresent(InMemoryMemberRepository.memberSet::add);

    for (Invitation invitation : group.getInvitations()) {
      InMemoryInvitationRepository.invitationMap.put(invitation.getId(), invitation);
    }
    return group;
  }

  @Override
  public Optional<Group> findById(Long id) {
    return Optional.ofNullable(groupIdMap.get(id));
  }

  @Override
  public Collection<Group> findAllGroupsByUserId(Long userId) {
      return groupIdMap.values().stream()
              .filter(group -> group.getMembers().stream().anyMatch(member -> member.getUserId().equals(userId)))
              .toList();
  }
}

class InMemoryGroupRoleRepository implements GroupRoleRepository, InMemoryRepository<GroupRole> {
  static final Map<String, GroupRole> groupRoleMap = new ConcurrentHashMap<>();

  InMemoryGroupRoleRepository() {
    addRoles();
  }

  @Override
  public Optional<GroupRole> findByName(GroupRoleName name) {
    return Optional.ofNullable(groupRoleMap.get(name.name()));
  }

  private void addRoles() {
    for (GroupRoleName roleName : GroupRoleName.values()) {
      GroupRole role = new GroupRole(roleName);
      groupRoleMap.put(roleName.name(), role);
    }
  }
}

class InMemoryInvitationRepository implements InvitationRepository, InMemoryRepository<Invitation> {
  static final Map<Long, Invitation> invitationMap = new ConcurrentHashMap<>();

  @Override
  public Invitation save(Invitation invitation) {
    invitationMap.put(invitation.getId(), invitation);
    return invitation;
  }

  @Override
  public int updateStatusById(Long id, InvitationStatus status) {
    var invitation = invitationMap.get(id);
    if (invitation == null) return 0;
    invitation.setStatus(status);
    if (status == InvitationStatus.ACCEPTED) {
      InMemoryMemberRepository.memberSet.add(new Member(invitation.getInviteeId(), "Nickname",
              new GroupRole(), invitation.getGroup()));
    }
    return 1;
  }

  @Override
  public Optional<Invitation> findById(Long id) {
    return Optional.ofNullable(invitationMap.get(id));
  }

  @Override
  public Collection<Invitation> findInvitationsByInviteeIdFilterByStatus(Long inviteeId, InvitationStatus status) {
    return invitationMap.values().stream()
            .filter(invitation -> inviteeId.equals(invitation.getInviteeId())
                    && (status == null || status.equals(invitation.getStatus())))
            .toList();
  }

  @Override
  public Collection<Invitation> findInvitationsByGroupIdFilterByStatus(Long groupId, InvitationStatus status) {
    return invitationMap.values().stream()
            .filter(invitation -> groupId.equals(invitation.getGroup().getId())
                    && (status == null || status.equals(invitation.getStatus())))
            .toList();
  }

  @Override
  public Optional<Invitation> findByGroupIdAndInviteeId(Long groupId, Long inviteeId) {
    return invitationMap.values().stream()
            .filter(invitation -> groupId.equals(invitation.getGroup().getId()) &&
                    inviteeId.equals(invitation.getInviteeId()))
            .findFirst();
  }
}

class InMemoryBillRepository implements BillRepository, InMemoryRepository<Bill> {
  Map<Long, Bill> billIdMap = new ConcurrentHashMap<>();

  @Override
  public Bill save(Bill bill) {
    return null;
  }

  @Override
  public Optional<Bill> findById(Long id) {
    return Optional.ofNullable(billIdMap.get(id));
  }
}


class InMemoryMemberRepository implements MemberRepository, InMemoryRepository<Member> {
  static final Set<Member> memberSet = new HashSet<>();

  @Override
  public Member save(Member member) {
    memberSet.add(member);
    return member;
  }

  @Override
  public Optional<Member> findByIdAndGroupId(Long memberId, Long groupId) {
    return memberSet.stream().filter(m -> m.getId().equals(memberId) && m.getGroup().getId().equals(groupId)).findFirst();
  }

  @Override
  public Set<Member> findAllByIdAndGroupId(Set<Long> ids, Long groupId) {
    return Set.of();
  }

  @Override
  public Optional<Member> findByUserIdAndGroupId(Long userId, Long groupId) {
    return memberSet.stream().filter(m -> m.getUserId().equals(userId) && m.getGroup().getId().equals(groupId)).findFirst();
  }
}