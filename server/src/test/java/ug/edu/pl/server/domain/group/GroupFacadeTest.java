package ug.edu.pl.server.domain.group;

import org.junit.jupiter.api.Test;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.group.dto.CreateInvitationsDto;
import ug.edu.pl.server.domain.user.SampleUsers;
import ug.edu.pl.server.domain.user.TestUserConfiguration;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;

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
    var invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
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
    var invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
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
    var invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
    assertThat(invitations).hasSize(0);
    // when
    groupFacade.createInvitations(new CreateInvitationsDto(inviteeIds, Long.valueOf(currentUser.id()), groupDto.id()));
    // then
    invitations = groupFacade.getInvitationsByGroupId(groupDto.id(), InvitationStatus.SENT);
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
}
