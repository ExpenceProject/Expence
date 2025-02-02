package ug.edu.pl.server.domain.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.group.dto.ExpenseDto;
import ug.edu.pl.server.domain.group.dto.GroupRoleDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "expenses")
class Expense extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "borrower_id", nullable = false)
  private Member borrower;

  @Column(nullable = false, precision = 6, scale = 2)
  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bill_id", nullable = false)
  private Bill bill;

  ExpenseDto dto() {
    var borrowerMember = borrower.dto();
    return new ExpenseDto(borrowerMember, amount);
  }
}
