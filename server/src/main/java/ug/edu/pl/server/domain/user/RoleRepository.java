package ug.edu.pl.server.domain.user;

import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.common.persistance.InMemoryRepository;
import ug.edu.pl.server.domain.user.exception.RoleNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

interface RoleRepository extends Repository<Role, Long> {

    Optional<Role> findByName(RoleName name);

    default Role findByNameOrThrow(RoleName name) {
        return findByName(name).orElseThrow(RoleNotFoundException::new);
    }
}

class InMemoryRoleRepository implements RoleRepository, InMemoryRepository<Role> {

    private final Map<String, Role> rolesNameMap = new ConcurrentHashMap<>();

    @Override
    public Optional<Role> findByName(RoleName name) {
        return Optional.ofNullable(rolesNameMap.get(name.name()));
    }

    public void addRoles() {
        for (RoleName roleName : RoleName.values()) {
            Role role = new Role(roleName);
            rolesNameMap.put(roleName.name(), role);
        }
    }
}
