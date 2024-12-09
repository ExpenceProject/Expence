package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryGroupRepository implements GroupRepository, InMemoryRepository<Group> {
  Map<Long, Group> groupIdMap = new ConcurrentHashMap<>();

  @Override
  public Group save(Group group) {
    updateTimestampsAndVersion(group);
    groupIdMap.put(group.getId(), group);
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
