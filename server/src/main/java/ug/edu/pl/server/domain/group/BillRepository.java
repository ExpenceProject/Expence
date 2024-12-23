package ug.edu.pl.server.domain.group;

import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.exception.SavingException;

import java.util.Optional;
import java.util.Set;

interface BillRepository extends Repository<Bill, Long> {
    Bill save(Bill bill);

    Optional<Bill> findById(Long id);

    default Bill findByIdOrThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(Bill.class.getName(), id));
    }

    default Bill saveOrThrow(Bill bill) {
        try {
            return save(bill);
        } catch (Exception ex) {
            throw new SavingException(ex.getMessage());
        }
    }
}
