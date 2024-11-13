package ug.edu.pl.server.domain.user;

import ug.edu.pl.server.base.InMemoryRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryUserRepository implements UserRepository, InMemoryRepository<User> {

    Map<Long, User> userIdMap = new ConcurrentHashMap<>();
    Map<String, User> userEmailMap = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        updateTimestampsAndVersion(user);
        userIdMap.put(user.getId(), user);
        userEmailMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userEmailMap.containsKey(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userIdMap.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userEmailMap.get(email));
    }
}

class InMemoryRoleRepository implements RoleRepository, InMemoryRepository<Role> {

    Map<String, Role> rolesNameMap = new ConcurrentHashMap<>();

    InMemoryRoleRepository() {
        addRoles();
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return Optional.ofNullable(rolesNameMap.get(name.name()));
    }

    private void addRoles() {
        for (RoleName roleName : RoleName.values()) {
            Role role = new Role(roleName);
            rolesNameMap.put(roleName.name(), role);
        }
    }
}
