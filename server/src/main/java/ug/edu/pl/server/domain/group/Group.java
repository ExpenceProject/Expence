package ug.edu.pl.server.domain.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.dto.ImageDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "groups")
class Group extends BaseEntity {

  @Embedded
  @AttributeOverride(name = "key", column = @Column(name = "image_key"))
  private Image image;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Boolean settledDown;

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<Member> members = new HashSet<>();

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<Invitation> invitations = new HashSet<>();

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<Bill> bills = new HashSet<>();

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<Payment> payments = new HashSet<>();

  GroupDto dto() {
    var imageDto = new ImageDto(image == null ? null : image.key());

    return GroupDto.builder()
        .id(getId())
        .image(imageDto)
        .name(name)
        .settledDown(settledDown)
        .version(getVersion())
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .build();
  }
}
