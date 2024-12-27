package ug.edu.pl.server.domain.group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.exception.SavingException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

interface GroupRepository extends Repository<Group, Long> {
  Group save(Group group);

  Optional<Group> findById(Long id);

  @Query("SELECT g FROM Group g JOIN g.members m WHERE m.userId = :userId")
  Collection<Group> findAllGroupsByUserId(Long userId);

  default Group findByIdOrThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(Group.class.getName(), id));
  }

  default Group saveOrThrow(Group group) {
    try {
      return save(group);
    } catch (Exception ex) {
      throw new SavingException(ex.getMessage());
    }
  }
}

interface GroupRoleRepository extends Repository<GroupRole, Long> {
  Optional<GroupRole> findByName(GroupRoleName name);

  default GroupRole findByNameOrThrow(GroupRoleName name) {
    return findByName(name).orElseThrow(() -> new NotFoundException(GroupRole.class.getName()));
  }
}

interface MemberRepository extends Repository<Member, Long> {
  Member save(Member member);

  Optional<Member> findByIdAndGroupId(Long memberId, Long groupId);

  Optional<Member> findByUserIdAndGroupId(Long userId, Long groupId);

  @Query("SELECT m FROM Member m WHERE m.id IN :ids AND m.group.id = :groupId")
  Set<Member> findAllByIdAndGroupId(@Param("ids") Set<Long> ids, @Param("groupId") Long groupId);

  default Member saveOrThrow(Member member) {
    try {
      return save(member);
    } catch (Exception ex) {
      throw new SavingException(ex.getMessage());
    }
  }

  default Member findByUserIdAndGroupIdOrThrow(Long userId, Long groupId) {
    return findByUserIdAndGroupId(userId, groupId).orElseThrow(() -> new NotFoundException(Member.class.getName()));
  }

  default Member findByIdAndGroupIdOrThrow(Long memberId, Long groupId) {
    return findByIdAndGroupId(memberId, groupId).orElseThrow(() -> new NotFoundException(Member.class.getName(), memberId));
  }

  default Set<Member> findAllByIdAndGroupIdOrThrow(Set<Long> memberIds, Long groupId) {
    Set<Member> members = new HashSet<>(findAllByIdAndGroupId(memberIds, groupId));

    if (members.size() != memberIds.size()) {
      throw new NotFoundException(Member.class.getName());
    }

    return members;
  }
}