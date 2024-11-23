package ug.edu.pl.server.domain.group;

import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.group.exception.GroupNotFoundException;
import ug.edu.pl.server.domain.group.exception.GroupRoleNotFoundException;
import ug.edu.pl.server.domain.group.exception.SavingGroupException;

import java.util.Optional;


interface GroupRepository extends Repository<Group, Long> {
    Group save(Group group);

    Optional<Group> findById(Long id);

    default Group findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    default Group saveOrThrow(Group group) {
        try {
            return save(group);
        } catch (Exception ex) {
            throw new SavingGroupException(ex.getMessage());
        }
    }
}

interface GroupRoleRepository extends Repository<GroupRole, Long> {
    Optional<GroupRole> findByName(GroupRoleName name);

    default GroupRole findByNameOrThrow(GroupRoleName name) {
        return findByName(name).orElseThrow(() -> new GroupRoleNotFoundException(name.toString()));
    }
}