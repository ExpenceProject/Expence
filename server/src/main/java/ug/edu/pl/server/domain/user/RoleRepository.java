package ug.edu.pl.server.domain.user;

import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.user.exception.RoleNotFoundException;

import java.util.Optional;

interface RoleRepository extends Repository<Role, Long> {

    Optional<Role> findByName(RoleName name);

    default Role findByNameOrThrow(RoleName name) {
        return findByName(name).orElseThrow(RoleNotFoundException::new);
    }
}
