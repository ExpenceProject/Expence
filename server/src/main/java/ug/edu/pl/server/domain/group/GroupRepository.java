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

  void deleteById(Long id);

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

  @Query("SELECT m.userId FROM Member m WHERE m.id = :memberId AND m.group.id = :groupId")
  String findUserIdByIdAndGroupId(String memberId, String groupId);

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


  @Query(value = """
    WITH amounts_owed_to_user AS (
        SELECT
            u.id AS userId,
            m.id AS memberId,
            m.nickname AS memberNickname,
            SUM(e.amount) - COALESCE(SUM(p.amount), 0) AS amountOwed
        FROM expenses e
                 JOIN bills b ON e.bill_id = b.id
                 JOIN members m ON e.borrower_id = m.id
                 JOIN users u ON m.user_id = u.id
                 LEFT JOIN payments p
                           ON p.receiver_id = b.lender_id
                               AND p.sender_id = m.id
                               AND p.group_id = b.group_id
        WHERE b.lender_id = :memberId AND m.id != :memberId
        GROUP BY u.id, m.id, m.nickname
    ),
         amounts_owed_by_user AS (
             SELECT
                 u.id AS userId,
                 m.id AS memberId,
                 m.nickname AS memberNickname,
                 SUM(e.amount) - COALESCE(SUM(p.amount), 0) AS amountOwed
             FROM expenses e
                      JOIN bills b ON e.bill_id = b.id
                      JOIN members m ON e.borrower_id = m.id
                      JOIN users u ON b.lender_id = u.id
                      LEFT JOIN payments p
                                ON p.receiver_id = m.id
                                    AND p.sender_id = b.lender_id
                                    AND p.group_id = b.group_id
             WHERE m.id = :memberId AND b.lender_id != :memberId
             GROUP BY u.id, m.id, m.nickname
         )
    SELECT
        COALESCE(a.userId, b.userId) AS userId,
        COALESCE(a.memberId, b.memberId) AS memberId,
        COALESCE(a.memberNickname, b.memberNickname) AS memberNickname,
        COALESCE(a.amountOwed, 0) - COALESCE(b.amountOwed, 0) AS amount
    FROM amounts_owed_to_user a
             FULL OUTER JOIN amounts_owed_by_user b ON a.userId = b.userId
    ORDER BY amount DESC
""", nativeQuery = true)
  Collection<Object[]> findMemberBalance(
          Long memberId
  );

  default Collection<Object[]> findMemberBalanceOrThrow(Long memberId){
    Collection<Object[]> balance = findMemberBalance(memberId);
    if(balance.isEmpty()){
      throw new NotFoundException(Member.class.getName(), memberId);
    }
    return balance;
  }

  @Query("SELECT CASE WHEN EXISTS (" +
          "    SELECT 1 " +
          "    FROM Expense e " +
          "    JOIN e.bill b " +
          "    WHERE (e.borrower.id = :memberId OR b.lender.id = :memberId) AND b.group.id = :groupId" +
          ") OR EXISTS (" +
          "    SELECT 1 " +
          "    FROM Payment p " +
          "    WHERE (p.sender.id = :memberId OR p.receiver.id = :memberId) AND p.group.id = :groupId" +
          ") THEN TRUE ELSE FALSE END " +
          "FROM Member m " +
          "WHERE m.id = :memberId AND m.group.id = :groupId")
  Boolean isMemberIncludedInGroupHistory(Long memberId, Long groupId);

}