package ug.edu.pl.server.domain.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.group.dto.InvitationDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "invitations")
class Invitation extends BaseEntity {
  @Column(nullable = false)
  private Long inviteeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inviter_id", nullable = false)
  private Member inviter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InvitationStatus status;

  InvitationDto dto() {
    return InvitationDto.builder()
            .id(getId())
            .inviteeId(inviteeId)
            .inviterId(inviter.getId())
            .groupId(group.getId())
            .status(status.name())
            .version(getVersion())
            .createdAt(getCreatedAt())
            .updatedAt(getUpdatedAt())
            .build();
  }
}
