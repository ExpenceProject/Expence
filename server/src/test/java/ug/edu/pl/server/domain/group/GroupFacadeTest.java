package ug.edu.pl.server.domain.group;

import org.junit.jupiter.api.Test;
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
    var groupDto = groupFacade.getById(Long.valueOf(group.id()));

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

    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));

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
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitations = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT);
    assertThat(invitations).hasSize(1);
  }

  @Test
  void shouldCreateInvitationsWhenGroupIsCreated() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    UserDto anotherInvitee = userFacade.create(SampleUsers.VALID_USER_3);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id()), Long.valueOf(anotherInvitee.id())));
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitations = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT);
    assertThat(invitations).hasSize(2);
  }

  @Test
  void shouldCreateInvitationsToExistingGroup() {
    // given
    Set<Long> inviteeIds = Set.of(
            Long.valueOf(userFacade.create(SampleUsers.ANOTHER_VALID_USER).id()),
            Long.valueOf(userFacade.create(SampleUsers.VALID_USER_3).id())
    );
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of());
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitations = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT);
    assertThat(invitations).hasSize(0);
    // when
    groupFacade.createInvitations(new CreateInvitationsDto(inviteeIds, Long.valueOf(currentUser.id()), Long.valueOf(groupDto.id())));

    // then
    invitations = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT);
    assertThat(invitations).hasSize(2);
  }

  @Test
  void shouldAddUserWhenAcceptingInvitation() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    // when
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    // then
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();
    assertThat(invitation).isPresent();
    assertThat(invitation.get().status()).isEqualTo(InvitationStatus.SENT.name());

    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()));
    assertThat(members).hasSize(1);
    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);
    // then
    invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.ACCEPTED).stream().findFirst();
    assertThat(invitation).isPresent();
    assertThat(invitation.get().status()).isEqualTo(InvitationStatus.ACCEPTED.name());
    // and
    members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()));
    assertThat(members).hasSize(2);
  }

  @Test
  void shouldReturnBillById() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    //when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    //then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    //given
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(Long.valueOf(members.get(1).id()), new BigDecimal(100)),
            new CreateExpenseDto(Long.valueOf(members.get(0).id()), new BigDecimal(50)));


    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), Long.valueOf(members.get(0).id()), Long.valueOf(groupDto.id()));

    var bill = groupFacade.createBill(createBillDto);

    //when
    var billDto = groupFacade.getBillById(Long.valueOf(bill.id()));

    //then
    assertThat(billDto).isEqualTo(bill);
  }

  @Test
  void shouldThrowExceptionWhenAskedForBillWithIdThatDoesNotExist() {
    // when & then
    assertThatThrownBy(() -> groupFacade.getBillById(1L))
            .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldCreateBill() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    //when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    //then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    //when
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(Long.valueOf(members.get(1).id()), new BigDecimal(100)),
            new CreateExpenseDto(Long.valueOf(members.get(0).id()), new BigDecimal(50)));


    CreateBillDto createBillDto =
            new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), Long.valueOf(members.get(0).id()), Long.valueOf(groupDto.id()));

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
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    //when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    //then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    //when
    Set<CreateExpenseDto> createExpenseDtos = Set.of(
            new CreateExpenseDto(Long.valueOf(members.get(1).id()), new BigDecimal(100)),
            new CreateExpenseDto(Long.valueOf(members.get(0).id()), new BigDecimal(50)));


    //then
    assertThatThrownBy(() -> groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(170), Long.valueOf(members.get(0).id()), Long.valueOf(groupDto.id()))))
            .isInstanceOf(SavingException.class);

  }

  @Test
  void shouldCreatePayment() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));

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
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    groupFacade.deletePayment(paymentDto.id());

    // then
    assertThatThrownBy(() -> groupFacade.getPaymentById(Long.valueOf(paymentDto.id())))
            .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldGetPaymentById() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));
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
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsBySenderIdAndGroupId(Long.valueOf(members.get(0).id()), Long.valueOf(groupDto.id()));

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldGetPaymentsByReceiverIdAndGroupId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsByReceiverIdAndGroupId(Long.valueOf(members.get(1).id()), Long.valueOf(groupDto.id()));

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldGetPaymentsByGroupId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsByGroupId(Long.valueOf(groupDto.id()));

    // then
    assertThat(payments).containsExactly(paymentDto);
  }

  @Test
  void shouldGetPaymentsByGroupIdAndSenderIdAndReceiverId() {
    // given
    UserDto invitee = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var groupToCreate = SampleGroups.validGroupWithFileAndInvitees(Set.of(Long.valueOf(invitee.id())));
    var groupDto = groupFacade.create(groupToCreate, currentUser);
    var invitation = groupFacade.getInvitationsByGroupId(Long.valueOf(groupDto.id()), InvitationStatus.SENT).stream().findFirst();

    // when
    groupFacade.updateInvitationStatus(invitation.get().id(), InvitationStatus.ACCEPTED, invitee);

    // then
    var members = groupFacade.findAllMembersByGroupId(Long.valueOf(groupDto.id()))
            .stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();

    assertThat(members).hasSize(2);

    // given
    CreatePaymentDto createPaymentDto = new CreatePaymentDto(Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()), new BigDecimal("100.00"), Long.valueOf(groupDto.id()));
    var paymentDto = groupFacade.createPayment(createPaymentDto);

    // when
    var payments = groupFacade.getPaymentsByGroupIdAndSenderIdAndReceiverId(Long.valueOf(groupDto.id()), Long.valueOf(members.get(0).id()), Long.valueOf(members.get(1).id()));

    // then
    assertThat(payments).containsExactly(paymentDto);
  }
}
