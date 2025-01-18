package ug.edu.pl.server.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.user.dto.RoleDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 40, unique = true, nullable = false)
    public RoleName name;

    RoleDto dto() {
        return new RoleDto(name.name());
    }
}
