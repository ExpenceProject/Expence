package ug.edu.pl.server.domain.group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ug.edu.pl.server.domain.common.exception.DeleteException;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.exception.SavingException;

import java.util.Collection;
import java.util.Optional;

interface BillRepository extends Repository<Bill, Long> {
    Bill save(Bill bill);

    Optional<Bill> findById(Long id);

    Collection<Bill> findAllByGroup_Id(Long groupId);

    @Query("SELECT b FROM Bill b " +
            "LEFT JOIN b.expenses e " +
            "WHERE (b.lender.userId = :userId OR e.borrower.userId = :userId) " +
            "AND b.group.id = :groupId")
    Collection<Bill> findAllByUserIdAndGroupId(Long userId, Long groupId);

    Void deleteById(Long id);

    default Void deleteByIdOrThrow(Long id) {
        try {
            return deleteById(id);
        } catch (Exception ex) {
            throw new DeleteException(ex.getMessage());
        }

    }

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