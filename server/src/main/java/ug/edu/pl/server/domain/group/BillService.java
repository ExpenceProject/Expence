package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.group.dto.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class BillService {
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    BillService(BillRepository billRepository, PaymentRepository paymentRepository,
                GroupRepository groupRepository, MemberRepository memberRepository) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    BillDto getById(Long id) {
        return billRepository.findByIdOrThrow(id).dto();
    }

    BillDto create(CreateBillDto billDto) {
        var lender = memberRepository.findByIdAndGroupIdOrThrow(billDto.lenderId(), billDto.groupId());
        var group = groupRepository.findByIdOrThrow(billDto.groupId());
        var bill = prepareBill(billDto, lender, group);
        var borrowers = findBorrowers(billDto);

        var expenses = prepareExpenses(billDto, bill, borrowers);
        bill.setExpenses(expenses);
        return billRepository.saveOrThrow(bill).dto();
    }

    private Bill prepareBill(CreateBillDto billDto, Member lender, Group group) {
        var bill = new Bill();
        bill.setName(billDto.name());
        bill.setTotalAmount(billDto.totalAmount());
        bill.setGroup(group);
        bill.setLender(lender);
        return bill;
    }

    private Map<Long, Member> findBorrowers(CreateBillDto billDto) {
        Set<Long> borrowerIds = billDto.expenses().stream()
                .map(CreateExpenseDto::borrowerId)
                .collect(Collectors.toSet());

        return memberRepository.findAllByIdAndGroupIdOrThrow(borrowerIds, billDto.groupId()).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
    }

    private Set<Expense> prepareExpenses(CreateBillDto billDto, Bill bill, Map<Long, Member> borrowers) {
        Set<Expense> expenses = new HashSet<>();
        for (CreateExpenseDto dto : billDto.expenses()) {
            var doesExist = bill.getExpenses().stream().anyMatch(expense -> expense.getBorrower().getId().equals(dto.borrowerId()));
            if(!doesExist) {
                var expense = new Expense();
                expense.setAmount(dto.amount());
                expense.setBorrower(borrowers.get(dto.borrowerId()));
                expense.setBill(bill);
                expenses.add(expense);
            } else {
                var expenseToAdd = bill.getExpenses().stream()
                        .filter(expense -> expense.getBorrower().getId().equals(dto.borrowerId()))
                        .findFirst()
                        .map(expense -> {
                            expense.setAmount(dto.amount());
                            expense.setBorrower(borrowers.get(dto.borrowerId()));
                            if (expense.getBill() == null) {
                                expense.setBill(bill);
                            }
                            return expense;
                        }).orElse(null);
                expenses.add(expenseToAdd);
            }
        }
        return expenses;
    }

    BillDto update(Long billId, CreateBillDto billDto) {
        Bill billToUpdate = billRepository.findByIdOrThrow(billId);
        billToUpdate.setName(billDto.name());
        billToUpdate.setTotalAmount(billDto.totalAmount());
        if(billToUpdate.getLender().getId() != billDto.lenderId()) {
            var lender = memberRepository.findByIdAndGroupIdOrThrow(billDto.lenderId(), billDto.groupId());
            billToUpdate.setLender(lender);
        }
        var newExpenses = billDto.expenses();
        var oldExpenses = billToUpdate.getExpenses();

        oldExpenses.removeIf(oldExpense ->
            newExpenses.stream().noneMatch(newExpense ->
                            Objects.equals(newExpense.borrowerId(), oldExpense.getBorrower().getId())
                    )
        );

        var borrowers = findBorrowers(billDto);

        var expenses = prepareExpenses(billDto, billToUpdate, borrowers);
        billToUpdate.setExpenses(expenses);
        return billRepository.saveOrThrow(billToUpdate).dto();
    }

    Collection<BillDto> getBillsByGroupId(Long groupId) {
        return billRepository.findAllByGroup_Id(groupId).stream().map(Bill::dto)
                .collect(Collectors.toList());
    }

    Collection<BillDto> getBillsByUserIdAndGroupId(Long userId, Long groupId) {
        return billRepository.findAllByUserIdAndGroupId(userId, groupId).stream().map(Bill::dto)
                .collect(Collectors.toList());
    }

    Void deleteBill(Long id) {
        return billRepository.deleteByIdOrThrow(id);
    }

    PaymentDto createPayment(CreatePaymentDto paymentDto) {
        var receiver = memberRepository.findByIdAndGroupIdOrThrow(paymentDto.receiverId(), paymentDto.groupId());
        var sender = memberRepository.findByIdAndGroupIdOrThrow(paymentDto.senderId(), paymentDto.groupId());
        var group = groupRepository.findByIdOrThrow(paymentDto.groupId());
        var payment = new Payment(receiver, sender, paymentDto.amount(), group);

        return paymentRepository.saveOrThrow(payment).dto();
    }

    PaymentDto getPaymentById(Long id) {
        return paymentRepository.findByIdOrThrow(id).dto();
    }

    Collection<PaymentDto> getPaymentsBySenderIdAndGroupId(Long senderId, Long groupId) {
        return paymentRepository.findAllBySenderIdAndGroupId(senderId, groupId).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Collection<PaymentDto> getPaymentsByReceiverIdAndGroupId(Long receiverId, Long groupId) {
        return paymentRepository.findAllByReceiverIdAndGroupId(receiverId, groupId).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Collection<PaymentDto> getPaymentsByGroupId(Long groupId) {
        return paymentRepository.findAllByGroupId(groupId).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Collection<PaymentDto> getPaymentsByGroupIdAndSenderIdAndReceiverId(Long groupId, Long senderId, Long receiverId) {
        return paymentRepository.findAllByGroupIdAndSenderIdAndReceiverId(groupId, senderId, receiverId).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Void deletePayment(Long id) {
        return paymentRepository.deleteById(id);
    }
}
