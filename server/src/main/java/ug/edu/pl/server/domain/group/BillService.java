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

    BillDto getById(String id) {
        return billRepository.findByIdOrThrow(Long.valueOf(id)).dto();
    }

    BillDto create(CreateBillDto billDto) {
        var lender = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(billDto.lenderId()), Long.valueOf(billDto.groupId()));
        var group = groupRepository.findByIdOrThrow(Long.valueOf(billDto.groupId()));
        var bill = prepareBill(billDto, lender, group);
        var borrowers = findBorrowers(billDto);

        prepareExpenses(billDto.expenses(), bill, borrowers);
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

    private Map<String, Member> findBorrowers(CreateBillDto billDto) {
        Set<Long> borrowerIds = billDto.expenses().stream()
                .map(expense -> Long.valueOf(expense.borrowerId()))
                .collect(Collectors.toSet());

        return memberRepository.findAllByIdAndGroupIdOrThrow(borrowerIds, Long.valueOf(billDto.groupId())).stream()
                .collect(Collectors.toMap(member -> String.valueOf(member.getId()), Function.identity()));
    }

    private void prepareExpenses(Set<CreateExpenseDto> newExpenses, Bill bill, Map<String, Member> borrowers) {
        for (CreateExpenseDto dto : newExpenses) {
            var doesExist = bill.getExpenses().stream().anyMatch(expense -> expense.getBorrower().getId().equals(dto.borrowerId()));
            if(!doesExist) {
                var expense = new Expense();
                expense.setAmount(dto.amount());
                expense.setBorrower(borrowers.get(dto.borrowerId()));
                expense.setBill(bill);
                bill.addExpense(expense);
            } else {
                bill.getExpenses().stream()
                        .filter(expense -> expense.getBorrower().getId().equals(dto.borrowerId()))
                        .findFirst()
                        .ifPresent(expense -> {
                            expense.setAmount(dto.amount());
                            expense.setBorrower(borrowers.get(dto.borrowerId()));
                            expense.setBill(bill);

                        });
            }
        }
    }

    BillDto update(String billId, CreateBillDto billDto) {
        Bill billToUpdate = billRepository.findByIdOrThrow(Long.valueOf(billId));
        billToUpdate.setName(billDto.name());
        billToUpdate.setTotalAmount(billDto.totalAmount());
        if(billToUpdate.getLender().getId().toString() != billDto.lenderId()) {
            var lender = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(billDto.lenderId()), Long.valueOf(billDto.groupId()));
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

        prepareExpenses(newExpenses, billToUpdate, borrowers);
        return billRepository.saveOrThrow(billToUpdate).dto();
    }

    Collection<BillDto> getBillsByGroupId(String groupId) {
        return billRepository.findAllByGroup_Id(Long.valueOf(groupId)).stream().map(Bill::dto)
                .collect(Collectors.toList());
    }

    Collection<BillDto> getBillsByUserIdAndGroupId(String userId, String groupId) {
        return billRepository.findAllByUserIdAndGroupId(Long.valueOf(userId), Long.valueOf(groupId)).stream().map(Bill::dto)
                .collect(Collectors.toList());
    }

    Void deleteBill(String id) {
        return billRepository.deleteByIdOrThrow(Long.valueOf(id));
    }

    PaymentDto createPayment(CreatePaymentDto paymentDto) {
        var receiver = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(paymentDto.receiverId()), Long.valueOf(paymentDto.groupId()));
        var sender = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(paymentDto.senderId()), Long.valueOf(paymentDto.groupId()));
        var group = groupRepository.findByIdOrThrow(Long.valueOf(paymentDto.groupId()));
        var payment = new Payment(receiver, sender, paymentDto.amount(), group);

        return paymentRepository.saveOrThrow(payment).dto();
    }

    PaymentDto getPaymentById(String id) {
        return paymentRepository.findByIdOrThrow(Long.valueOf(id)).dto();
    }

    Collection<PaymentDto> getPaymentsBySenderIdAndGroupId(String senderId, String groupId) {
        return paymentRepository.findAllBySenderIdAndGroupId(Long.valueOf(senderId), Long.valueOf(groupId)).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Collection<PaymentDto> getPaymentsByReceiverIdAndGroupId(String receiverId, String groupId) {
        return paymentRepository.findAllByReceiverIdAndGroupId(Long.valueOf(receiverId), Long.valueOf(groupId)).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Collection<PaymentDto> getPaymentsByGroupId(String groupId) {
        return paymentRepository.findAllByGroupIdOrderByCreatedAtDesc(Long.valueOf(groupId)).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Collection<PaymentDto> getPaymentsByGroupIdAndSenderIdAndReceiverId(String groupId, String senderId, String receiverId) {
        return paymentRepository.findAllByGroupIdAndSenderIdAndReceiverId(Long.valueOf(groupId), Long.valueOf(senderId), Long.valueOf(receiverId)).stream().map(Payment::dto)
                .collect(Collectors.toList());
    }

    Void deletePayment(String id) {
        return paymentRepository.deleteById(Long.valueOf(id));
    }
}
