package ug.edu.pl.server.domain.group;

import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.group.exception.BillNotFoundException;
import ug.edu.pl.server.domain.group.exception.SavingBillException;

import java.util.Optional;
import java.util.Set;

interface BillRepository extends Repository<Bill, Long> {
    Bill save(Bill bill);

    Optional<Bill> findById(Long id);

    default Bill findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BillNotFoundException(id));
    }

    default Bill saveOrThrow(Bill bill) {
        try {
            return save(bill);
        } catch (Exception ex) {
            throw new SavingBillException(ex.getMessage());
        }
    }
}

interface ExpenseRepository extends Repository<Expense, Long> {
    Set<Expense> findByBillId(Long id);

    default Set<Expense> findByBillIdOrThrow(Long id) {
        return findByBillId(id);
    }
}
