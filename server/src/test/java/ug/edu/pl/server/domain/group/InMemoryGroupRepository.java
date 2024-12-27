package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class InMemoryGroupRepository implements GroupRepository, InMemoryRepository<Group> {
  static final Map<Long, Group> groupIdMap = new ConcurrentHashMap<>();

  @Override
  public Group save(Group group) {
    updateTimestampsAndVersion(group);
    groupIdMap.put(group.getId(), group);

    for (Member member : group.getMembers()) {
      InMemoryMemberRepository.memberMap.put(member.getId(), member);
    }

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
    updateTimestampsAndVersion(bill);
    billIdMap.put(bill.getId(), bill);

    for (Expense expense : bill.getExpenses()) {
      InMemoryExpenseRepository.expenseMap.put(expense.getId(), expense);
    }
    return bill;
  }

  @Override
  public Optional<Bill> findById(Long id) {
    return Optional.ofNullable(billIdMap.get(id));
  }
}

class InMemoryExpenseRepository implements ExpenseRepository, InMemoryRepository<Expense> {
  static final Map<Long, Expense> expenseMap = new ConcurrentHashMap<>();

  @Override
  public Expense save(Expense expense) {
    updateTimestampsAndVersion(expense);
    expenseMap.put(expense.getId(), expense);
    return expense;
  }
}


class InMemoryMemberRepository implements MemberRepository, InMemoryRepository<Member> {
  static final Map<Long, Member> memberMap = new ConcurrentHashMap<>();

  @Override
  public Member save(Member member) {
    memberMap.put(member.getId(), member);
    return member;
  }

  @Override
  public Optional<Member> findByIdAndGroupId(Long memberId, Long groupId) {
    Member member = memberMap.get(memberId);
    if (member != null && member.getGroup().getId().equals(groupId)) {
      return Optional.of(member);
    }
    return Optional.empty();
  }

  @Override
  public Set<Member> findAllByIdAndGroupId(Set<Long> ids, Long groupId) {
    return memberMap.values().stream()
            .filter(m -> ids.contains(m.getId()) && m.getGroup().getId().equals(groupId))  // Filtrujemy po ID i grupie
            .collect(Collectors.toSet());
  }

  @Override
  public Optional<Member> findByUserIdAndGroupId(Long userId, Long groupId) {
    return memberMap.values().stream().filter(m -> m.getUserId().equals(userId) && m.getGroup().getId().equals(groupId)).findFirst();
  }
}