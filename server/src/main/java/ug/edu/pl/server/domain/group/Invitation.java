package ug.edu.pl.server.domain.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "invitations")
class Invitation extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "invitee_id", nullable = false)
    private Member invitee;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private Member inviter;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;
}
