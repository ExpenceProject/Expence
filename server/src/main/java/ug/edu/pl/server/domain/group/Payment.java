package ug.edu.pl.server.domain.group;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.group.dto.PaymentDto;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "payments")
class Payment extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = false)
  private Member receiver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private Member sender;

  @Column(nullable = false, precision = 6, scale = 2)
  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  PaymentDto dto() {
    return PaymentDto.builder()
            .id(getId().toString())
            .receiver(receiver.dto())
            .sender(sender.dto())
            .amount(amount)
            .group(group.dto())
            .version(getVersion())
            .createdAt(getCreatedAt())
            .updatedAt(getUpdatedAt())
            .build();
  }
}
