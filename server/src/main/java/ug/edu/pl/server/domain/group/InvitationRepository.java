package ug.edu.pl.server.domain.group;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.exception.SavingException;

import java.util.Collection;
import java.util.Optional;

interface InvitationRepository extends Repository<Invitation, Long> {
    Invitation save(Invitation invitation);

    Optional<Invitation> findById(Long id);

    Optional<Invitation> findByGroupIdAndInviteeId(@Param("groupId") Long groupId, @Param("inviteeId") Long inviteeId);

    @Query("SELECT i FROM Invitation i WHERE i.inviteeId = :inviteeId AND (:status IS NULL OR i.status = :status)")
    Collection<Invitation> findInvitationsByInviteeIdFilterByStatus(@Param("inviteeId") Long inviteeId, @Param("status") InvitationStatus status);


    @Query("SELECT i FROM Invitation i WHERE i.group.id = :groupId AND (:status IS NULL OR i.status = :status)")
    Collection<Invitation> findInvitationsByGroupIdFilterByStatus(@Param("groupId") Long groupId, @Param("status") InvitationStatus status);

    @Modifying
    @Query("UPDATE Invitation i SET i.status = :status WHERE i.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") InvitationStatus status);

    default Invitation saveOrThrow(Invitation invitation) {
        try {
            return save(invitation);
        } catch (Exception ex) {
            throw new SavingException(ex.getMessage());
        }
    }

    default Invitation findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException(Invitation.class.getName(), id));
    }

    default Invitation findByGroupAndInviteeIdOrThrow(Long groupId, Long inviteeId) {
        return findByGroupIdAndInviteeId(groupId, inviteeId).orElseThrow(() -> new NotFoundException(Invitation.class.getName()));
    }

    default void updateStatusByIdOrThrow(Long id, InvitationStatus status) {
        try {
            int updatedRows = updateStatusById(id, status);
            if (updatedRows == 0) {
                throw new NotFoundException(Invitation.class.getName(), id);
            }
        } catch (Exception ex) {
            throw new SavingException("Failed to update status for Invitation with id " + id + ": " + ex.getMessage());
        }
    }
}
