package ug.edu.pl.server.domain.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ug.edu.pl.server.domain.common.persistance.BaseEntity;
import ug.edu.pl.server.domain.group.dto.BillDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "bills")
class Bill extends BaseEntity {
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Expense> expenses = new HashSet<>();

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lender_id", nullable = false)
  private Member lender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  void addExpense(Expense expense) {
    this.expenses.add(expense);
    expense.setBill(this);
  }

  BillDto dto() {
    var expensesSet = expenses.stream()
            .map(Expense::dto)
            .collect(Collectors.toSet());

    var lenderDto = lender.dto();

    return BillDto.builder()
            .id(getId().toString())
            .name(name)
            .expenses(expensesSet)
            .totalAmount(totalAmount)
            .lender(lenderDto)
            .groupId(getGroup().getId().toString())
            .version(getVersion())
            .createdAt(getCreatedAt())
            .updatedAt(getUpdatedAt())
            .build();
  }
}
