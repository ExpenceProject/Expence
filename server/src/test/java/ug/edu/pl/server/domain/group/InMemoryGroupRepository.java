package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryGroupRepository implements GroupRepository, InMemoryRepository<Group>  {
  private final InMemoryInvitationRepository invitationRepository;

  public InMemoryGroupRepository(InMemoryInvitationRepository invitationRepository) {
    this.invitationRepository = invitationRepository;
  }
  Map<Long, Group> groupIdMap = new ConcurrentHashMap<>();

  @Override
  public Group save(Group group) {
    updateTimestampsAndVersion(group);
    groupIdMap.put(group.getId(), group);
    for (Invitation invitation : group.getInvitations()) {
      invitationRepository.save(invitation);
    }
    return group;
  }

  @Override
  public Optional<Group> findById(Long id) {
    return Optional.ofNullable(groupIdMap.get(id));
  }
}

class InMemoryGroupRoleRepository implements GroupRoleRepository, InMemoryRepository<GroupRole> {
  Map<String, GroupRole> groupRoleMap = new ConcurrentHashMap<>();

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
  Map<Long, Invitation> invitationMap = new ConcurrentHashMap<>();

  // TODO: override
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
  public Collection<Invitation> findInvitationsByInviteeId(Long inviteeId) {
    return invitationMap.values().stream()
            .filter(invitation -> inviteeId.equals(invitation.getInviteeId()))
            .toList();
  }

  @Override
  public Collection<Invitation> findInvitationsByGroupId(Long groupId) {
    return invitationMap.values().stream()
            .filter(invitation -> groupId.equals(invitation.getGroup().getId()))
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