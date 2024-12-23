package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.group.dto.BillDto;
import ug.edu.pl.server.domain.group.dto.CreateBillDto;
import ug.edu.pl.server.domain.group.dto.CreateExpenseDto;
import ug.edu.pl.server.domain.group.dto.ExpenseDto;

import java.util.HashSet;
import java.util.Set;

class BillService {
    private final BillRepository billRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    BillService(BillRepository billRepository, GroupRepository groupRepository, MemberRepository memberRepository) {
        this.billRepository = billRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    BillDto getById(Long id) {
        return billRepository.findByIdOrThrow(id).dto();
    }

    BillDto create(CreateBillDto billDto) {
        var lender = memberRepository.findByIdAndGroupIdOrThrow(billDto.lenderId(), billDto.groupId());
        var group = groupRepository.findByIdOrThrow(billDto.groupId());

        var bill = new Bill();
        bill.setName(billDto.name());
        bill.setTotalAmount(billDto.totalAmount());
        bill.setGroup(group);
        bill.setLender(lender);

        Set<Expense> expenses = new HashSet<>();
        for (CreateExpenseDto dto : billDto.expenses()) {
            var borrower = memberRepository.findByIdAndGroupIdOrThrow(dto.borrowerId(), billDto.groupId());
            var expense = new Expense();
            expense.setAmount(dto.amount());
            expense.setBorrower(borrower);
            expense.setBill(bill);
            expenses.add(expense);
        }
        bill.setExpenses(expenses);
        return billRepository.saveOrThrow(bill).dto();
    }
}
