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
@Table(name = "members")
class Member extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @OneToOne(optional = false)
    @JoinColumn(name = "group_role_id", nullable = false)
    private GroupRole groupRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;


}
