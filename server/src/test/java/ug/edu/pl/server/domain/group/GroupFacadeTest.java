package ug.edu.pl.server.domain.group;

import org.junit.jupiter.api.Test;
import ug.edu.pl.server.domain.common.exception.ForbiddenException;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.exception.SavingException;
import ug.edu.pl.server.domain.group.dto.*;
import ug.edu.pl.server.domain.user.SampleUsers;
import ug.edu.pl.server.domain.user.TestUserConfiguration;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupFacadeTest {
  UserFacade userFacade = new TestUserConfiguration().userFacadeForGroup();
  GroupFacade groupFacade = new TestGroupConfiguration().groupFacade(userFacade);
  UserDto currentUser = userFacade.create(SampleUsers.VALID_USER);


  @Test
  void shouldReturnGroupById() {
    // given
    var group = groupFacade.create(SampleGroups.VALID_GROUP_NO_FILE_AND_INVITEES, currentUser);

    // when
    var groupDto = groupFacade.getById(group.id());

    // then
    assertThat(groupDto).isEqualTo(group);
  }

  @Test
  void shouldThrowExceptionWhenAskedForGroupWithIdThatDoesNotExist() {
    // when & then
    assertThatThrownBy(() -> groupFacade.getById(SampleGroups.ID_THAT_DOES_NOT_EXIST))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldCreateGroupWithNoFileAndInvitees() {
    // given
    var groupToCreate = SampleGroups.VALID_GROUP_NO_FILE_AND_INVITEES;

    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);

    // then
    assertThat(groupDto.id()).isNotNull();
    assertThat(groupDto.name()).isEqualTo(groupToCreate.name());
    assertThat(groupDto.settledDown()).isFalse();
    assertThat(groupDto.image()).isNotNull();
    assertThat(groupDto.image().key()).isNull();
  }

  @Test
  void shouldCreateGroupWithFileAndNoInvitees() {
    // given
    var groupToCreate = SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES;

    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);

    // then
    assertThat(groupDto.id()).isNotNull();
    assertThat(groupDto.name()).isEqualTo(groupToCreate.name());
    assertThat(groupDto.settledDown()).isFalse();
    assertThat(groupDto.image()).isNotNull();
    assertThat(groupDto.image().key()).isNotNull();
  }

  @Test
  void shouldCreateGroupWithFileAndInvitees() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);

    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));

    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);

    // then
    assertThat(groupDto.id()).isNotNull();
    assertThat(groupDto.name()).isEqualTo(groupToCreate.name());
    assertThat(groupDto.settledDown()).isFalse();
    assertThat(groupDto.image()).isNotNull();
    assertThat(groupDto.image().key()).isNotNull();
  }

  @Test
  void shouldCreateInvitationWhenGroupIsCreated() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
    assertThat(invitations).hasSize(1);
  }

  @Test
  void shouldCreateInvitationsWhenGroupIsCreated() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    UserDto anotherInvitee = userFacade.create(SampleUsers.VALID_USER_3);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id(), anotherInvitee.id()));
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
    assertThat(invitations).hasSize(2);
  }

  @Test
  void shouldCreateInvitationsToExistingGroup() {
    // given
    Set<String> inviteeIds = Set.of(
            userFacade.create(SampleUsers.ANOTHER_VALID_USER).id(),
            userFacade.create(SampleUsers.VALID_USER_3).id()
    );
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of());
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
    assertThat(invitations).hasSize(0);
    // when
    groupFacade.createInvitations(new CreateInvitationsDto(inviteeIds, currentUser.id(), groupDto.id()));

    // then
    invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
    assertThat(invitations).hasSize(2);
  }

  @Test
  void shouldAddUserWhenAcceptingInvitation() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();
    assertThat(invitation).isPresent();
    assertThat(invitation.get().status()).isEqualTo(InvitationStatus.SENT.name());

    var members = groupFacade.findAllMembersByGroupId(groupDto.id());
    assertThat(members).hasSize(1);
    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);
    // then
    invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.ACCEPTED).stream().findFirst();
    assertThat(invitation).isPresent();
    assertThat(invitation.get().status()).isEqualTo(InvitationStatus.ACCEPTED.name());
    // and
    members = groupFacade.findAllMembersByGroupId(groupDto.id());
    assertThat(members).hasSize(2);
  }

  @Test
  void shouldReturnBillById() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    //when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    //then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    //given
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));


    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    var bill = groupFacade.createBill(createBillDto);

    //when
    var billDto = groupFacade.getBillById(bill.id());

    //then
    assertThat(billDto).isEqualTo(bill);
  }

  @Test
  void shouldThrowExceptionWhenAskedForBillWithIdThatDoesNotExist() {
    // when & then
    assertThatThrownBy(() -> groupFacade.getBillById("1"))
            .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldCreateBill() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    //when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    //then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    //when
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));


    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    var bill = groupFacade.createBill(createBillDto);

    //then
    assertThat(bill.id()).isNotNull();
    assertThat(bill.name()).isEqualTo(createBillDto.name());
    assertThat(bill.totalAmount()).isEqualTo(createBillDto.totalAmount());
    assertThat(bill.groupId()).isEqualTo(createBillDto.groupId());
    assertThat(bill.expenses()).hasSize(2);
  }

  @Test
  void shouldThrowExceptionWhenCreatingBillWithDifferentTotalAmountAndSumOfAmounts() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    //when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    //then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    //when
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));


    //then
    assertThatThrownBy(() -> groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(170), members.get(0).id(), groupDto.id())))
            .isInstanceOf(SavingException.class);

  }

  @Test
  void shouldGetBillsByGroupId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given

    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    var bill = groupFacade.createBill(createBillDto);

    // when
    var billRetrieved = groupFacade.getBillsByGroupId(groupDto.id());

    // then
    assertThat(billRetrieved).containsExactly(bill);
  }


  @Test
  void shouldGetBillsByGroupIdAndUserId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given

    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    var bill = groupFacade.createBill(createBillDto);

    // when
    var billRetrieved = groupFacade.getBillsByUserIdAndGroupId(invitee.id(), groupDto.id());

    // then
    assertThat(billRetrieved).containsExactly(bill);
  }

  @Test
  void shouldDeleteBill() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    var bill = groupFacade.createBill(createBillDto);


    // when
    groupFacade.deleteBill(bill.id());

    // then
    assertThatThrownBy(() -> groupFacade.getBillById(bill.id()))
            .isInstanceOf(NotFoundException.class);
  }


  @Test
  void shouldUpdateBill() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // when
    Set<CreateExpenseDto> initialExpenses = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    CreateBillDto initialBillDto =
            new CreateBillDto("initial bill", initialExpenses, new BigDecimal(150), members.get(0).id(), groupDto.id());

    var initialBill = groupFacade.createBill(initialBillDto);

    // then
    assertThat(initialBill.id()).isNotNull();
    assertThat(initialBill.name()).isEqualTo(initialBillDto.name());
    assertThat(initialBill.totalAmount()).isEqualTo(initialBillDto.totalAmount());
    assertThat(initialBill.expenses()).hasSize(2);

    // when
    Set<CreateExpenseDto> updatedExpenses = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(50)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(150))
    );

    CreateBillDto updatedBillDto =
            new CreateBillDto("updated bill", updatedExpenses, new BigDecimal(300), members.get(0).id(), groupDto.id());

    var updatedBill = groupFacade.updateBill(updatedBillDto, initialBill.id());

    // then
    assertThat(updatedBill.id()).isEqualTo(initialBill.id()); // ID remains the same
    assertThat(updatedBill.name()).isEqualTo(updatedBillDto.name());
    assertThat(updatedBill.totalAmount()).isEqualTo(updatedBillDto.totalAmount());
    assertThat(updatedBill.expenses()).hasSize(3);

    var updatedExpenseList = updatedBill.expenses().stream()
            .map(ExpenseDto::amount)
            .toList();


    assertThat(updatedExpenseList).contains(new BigDecimal(50));
    assertThat(updatedExpenseList).contains(new BigDecimal(100));
    assertThat(updatedExpenseList).contains(new BigDecimal(150));
  }



  @Test
  void shouldCreatePayment() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());

    // when
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // then
    assertThat(paymentDto.id()).isNotNull();
    assertThat(paymentDto.sender().id()).isEqualTo(members.get(0).id());
    assertThat(paymentDto.receiver().id()).isEqualTo(members.get(1).id());
    assertThat(paymentDto.amount()).isEqualTo(new BigDecimal("100.00"));
    assertThat(paymentDto.group().id()).isEqualTo(groupDto.id());
    assertThat(paymentDto.version()).isNotNull();
    assertThat(paymentDto.createdAt()).isNotNull();
    assertThat(paymentDto.updatedAt()).isNotNull();
  }

  @Test
  void shouldDeletePayment() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    groupFacade.deletePayment(paymentDto.id());

    // then
    assertThatThrownBy(() -> groupFacade.getPaymentById(paymentDto.id()))
            .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldGetPaymentById() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var retrievedPayment = groupFacade.getPaymentById(paymentDto.id());

    // then
    assertThat(retrievedPayment).isEqualTo(paymentDto);
  }

  @Test
  void shouldGetPaymentsBySenderIdAndGroupId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsBySenderIdAndGroupId(members.get(0).id(), groupDto.id());

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldGetPaymentsByReceiverIdAndGroupId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsByReceiverIdAndGroupId(members.get(1).id(), groupDto.id());

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldGetPaymentsByGroupId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsByGroupId(groupDto.id());

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldGetPaymentsByGroupIdAndSenderIdAndReceiverId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsByGroupIdAndSenderIdAndReceiverId(groupDto.id(), members.get(0).id(), members.get(1).id());

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldNotDeleteMemberWhenCheckingIfUserIsIncludedInGroupHistoryWhenMemberAssociatedWithPayment() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    groupFacade.createPayment(createPaymentDto);


    // when & then
    assertThatThrownBy(() -> groupFacade.deleteMember(groupDto.id(), members.get(0).id()))
            .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void shouldDeleteMemberWhenCheckingIfUserIsIncludedInGroupHistoryWhenMemberNotAssociatedWithPayment() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    UserDto anotherInvitee = userFacade.create(SampleUsers.VALID_USER_3);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id(), anotherInvitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByInviteeId(invitee.id(), InvitationStatus.SENT).stream().findFirst();
    var secondInvitation = groupFacade.getInvitationsByInviteeId(anotherInvitee.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);
    groupFacade.updateInvitationStatus(secondInvitation.get().id(), InvitationStatus.ACCEPTED, anotherInvitee);


    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(3);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(members.get(0).id(), members.get(1).id(), new BigDecimal("100.00"), groupDto.id());
    groupFacade.createPayment(createPaymentDto);


    // when & then
    groupFacade.deleteMember(groupDto.id(), members.get(2).id());
  }

  @Test
  void shouldNotDeleteMemberWhenCheckingIfUserIsIncludedInGroupHistoryWhenMemberAssociatedWithExpenses() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));


    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    groupFacade.createBill(createBillDto);

    // when & then
    assertThatThrownBy(() -> groupFacade.deleteMember(groupDto.id(), members.get(0).id()))
            .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void shouldDeleteMemberWhenCheckingIfUserIsIncludedInGroupHistoryWhenMemberNotAssociatedWithExpenses() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    UserDto anotherInvitee = userFacade.create(SampleUsers.VALID_USER_3);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(invitee.id(), anotherInvitee.id()));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByInviteeId(invitee.id(), InvitationStatus.SENT).stream().findFirst();
    var secondInvitation = groupFacade.getInvitationsByInviteeId(anotherInvitee.id(), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);
    groupFacade.updateInvitationStatus(secondInvitation.get().id(), InvitationStatus.ACCEPTED, anotherInvitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(groupDto.id())
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(3);

    // given
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
            new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));


    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), groupDto.id());

    groupFacade.createBill(createBillDto);

    // when & then
    groupFacade.deleteMember(groupDto.id(), members.get(2).id());
  }
}
