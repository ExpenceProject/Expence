package ug.edu.pl.server.domain.group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.exception.SavingException;

import java.util.Collection;
import java.util.Optional;

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

interface ExpenseRepository extends Repository<Expense, Long> {
    Expense save(Expense expense);

    default Expense saveOrThrow(Expense expense) {
        try {
            return save(expense);
        } catch (Exception ex) {
            throw new SavingException(ex.getMessage());
        }
    }
}

interface PaymentRepository extends Repository<Payment, Long> {
    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    @Query("SELECT p FROM Payment p WHERE p.sender.id = :senderId AND p.group.id = :groupId")
    Collection<Payment> findAllBySenderIdAndGroupId(Long senderId, Long groupId);

    @Query("SELECT p FROM Payment p WHERE p.receiver.id = :receiverId AND p.group.id = :groupId")
    Collection<Payment> findAllByReceiverIdAndGroupId(Long receiverId, Long groupId);

    @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId")
    Collection<Payment> findAllByGroupId(Long groupId);

    @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId AND p.sender.id = :senderId AND p.receiver.id = :receiverId")
    Collection<Payment> findAllByGroupIdAndSenderIdAndReceiverId(Long groupId, Long senderId, Long receiverId);

    Void deleteById(Long id);

    default Payment saveOrThrow(Payment payment) {
        try {
            return save(payment);
        } catch (Exception ex) {
            throw new SavingException(ex.getMessage());
        }
    }

    default Payment findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException(Payment.class.getName(), id));
    }
}