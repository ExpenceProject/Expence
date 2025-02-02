package ug.edu.pl.server.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.dto.ImageDto;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "users")
class User extends BaseEntity {

    @Column(nullable = false, unique = true, columnDefinition = "email_address")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    @Embedded
    @AttributeOverride(name = "key", column = @Column(name = "image_key"))
    private Image image;

    @ManyToMany(cascade = {CascadeType.REFRESH})
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    UserDto dto() {
        var roleDtoSet = roles.stream()
                .map(Role::dto)
                .collect(Collectors.toSet());

        var imageDto = new ImageDto(image == null ? null : image.key());

        return UserDto.builder()
                .id(getId().toString())
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .image(imageDto)
                .roles(roleDtoSet)
                .version(getVersion())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
