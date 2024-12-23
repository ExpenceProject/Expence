package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.group.dto.BillDto;
import ug.edu.pl.server.domain.group.dto.CreateBillDto;
import ug.edu.pl.server.domain.group.dto.CreateExpenseDto;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            var expense = new Expense();
            expense.setAmount(dto.amount());
            expense.setBorrower(borrowers.get(dto.borrowerId()));
            expense.setBill(bill);
            expenses.add(expense);
        }
        return expenses;
    }
}
