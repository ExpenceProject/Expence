package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.*;
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

  @Override
  public void deleteById(Long id) {
    groupIdMap.remove(id);
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

    if (billIdMap.containsKey(bill.getId())) {
      Set<Expense> currentExpenses = new HashSet<>(bill.getExpenses());
      Set<Expense> existingExpenses = new HashSet<>(billIdMap.get(bill.getId()).getExpenses());

      existingExpenses.removeIf(expense -> !currentExpenses.contains(expense));
      for (Expense expense : existingExpenses) {
        InMemoryExpenseRepository.expenseMap.remove(expense.getId());  // Remove orphaned expenses
      }
    } else {
      billIdMap.put(bill.getId(), bill);
    }

    for (Expense expense : bill.getExpenses()) {
      if (InMemoryExpenseRepository.expenseMap.containsKey(expense.getId())) {
        InMemoryExpenseRepository.expenseMap.put(expense.getId(), expense);
      } else {
        InMemoryExpenseRepository.expenseMap.put(expense.getId(), expense);
      }
    }

    billIdMap.put(bill.getId(), bill);

    return bill;
  }

  @Override
  public Optional<Bill> findById(Long id) {
    return Optional.ofNullable(billIdMap.get(id));
  }

  @Override
  public Collection<Bill> findAllByGroup_Id(Long groupId) {
    return billIdMap.values().stream()
            .filter(bill -> bill.getGroup().getId().equals(groupId))
            .collect(Collectors.toList());
  }

  @Override
  public Collection<Bill> findAllByUserIdAndGroupId(Long userId, Long groupId) {
    return billIdMap.values().stream()
            .filter(bill -> bill.getGroup().getId().equals(groupId) && (bill.getLender().getUserId().equals(userId) || bill.getExpenses().stream().anyMatch(expense -> expense.getBorrower().getUserId().equals(userId))))
            .collect(Collectors.toList());
  }

  @Override
  public Void deleteById(Long id) {
    billIdMap.remove(id);
    return null;
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

class InMemoryPaymentRepository implements PaymentRepository, InMemoryRepository<Payment> {
  static final Map<Long, Payment> paymentMap = new ConcurrentHashMap<>();

  @Override
  public Payment save(Payment payment) {
    updateTimestampsAndVersion(payment);
    paymentMap.put(payment.getId(), payment);
    return payment;
  }

  @Override
  public Optional<Payment> findById(Long id) {
    return Optional.ofNullable(paymentMap.get(id));
  }

  @Override
  public Collection<Payment> findAllBySenderIdAndGroupId(Long senderId, Long groupId) {
    return paymentMap.values().stream()
            .filter(payment -> payment.getSender().getId().equals(senderId) && payment.getGroup().getId().equals(groupId))
            .collect(Collectors.toList());
  }

  @Override
  public Collection<Payment> findAllByReceiverIdAndGroupId(Long receiverId, Long groupId) {
    return paymentMap.values().stream()
            .filter(payment -> payment.getReceiver().getId().equals(receiverId) && payment.getGroup().getId().equals(groupId))
            .collect(Collectors.toList());
  }

  @Override
  public Collection<Payment> findAllByGroupId(Long groupId) {
    return paymentMap.values().stream()
            .filter(payment -> payment.getGroup().getId().equals(groupId))
            .collect(Collectors.toList());
  }

  @Override
  public Collection<Payment> findAllByGroupIdAndSenderIdAndReceiverId(Long groupId, Long senderId, Long receiverId) {
    return paymentMap.values().stream()
            .filter(payment -> payment.getGroup().getId().equals(groupId) &&
                    payment.getSender().getId().equals(senderId) &&
                    payment.getReceiver().getId().equals(receiverId))
            .collect(Collectors.toList());
  }

  @Override
  public Void deleteById(Long id) {
    paymentMap.remove(id);
    return null;
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