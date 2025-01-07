package ug.edu.pl.server.infrastructure.group.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.InvitationStatus;
import ug.edu.pl.server.domain.group.SampleGroups;
import ug.edu.pl.server.domain.group.dto.CreateBillDto;
import ug.edu.pl.server.domain.group.dto.CreateExpenseDto;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.MemberDto;
import ug.edu.pl.server.domain.user.SampleUsers;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.SampleRegisterUsers;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupControllerTest extends IntegrationTest {

  static final String URL = "/api/groups";

  @Autowired GroupFacade groupFacade;

  @Autowired UserFacade userFacade;

  @Autowired AuthFacade authFacade;

  private UserDto registeredUser;

  @BeforeEach
  void setUp() {
    registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldGetById() throws Exception {
    // given
    var createdGroup =
        groupFacade.create(SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES, registeredUser);

    // when
    var result = getById(createdGroup.id());

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(createdGroup.name()))
        .andExpect(jsonPath("$.id").value(createdGroup.id()))
        .andExpect(jsonPath("$.settledDown").value(createdGroup.settledDown()))
        .andExpect(jsonPath("$.image.key").value(createdGroup.image().key()));
  }

  @Test
  @Transactional
  void shouldReturnUnauthorizedWhenGetByIdWithoutLogin() throws Exception {
    // given
    var createdGroup =
        groupFacade.create(SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES, registeredUser);
    // when
    var result = getById(createdGroup.id());
    // then
    result.andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldReturnNotFoundWhenGetByIdThatDoesNotExist() throws Exception {
    // when
    var result = getById(SampleGroups.ID_THAT_DOES_NOT_EXIST);

    // then
    result.andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldCreateGroup() throws Exception {
    // when
    var authenticatedUser =
        authFacade.authenticateAndGenerateToken(
            new LoginDto(
                SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
    var result = create(SampleGroups.VALID_GROUP_NO_FILE_AND_INVITEES, authenticatedUser);

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value(SampleGroups.VALID_GROUP_NO_FILE_AND_INVITEES.name()))
        .andExpect(jsonPath("$.settledDown").value(false))
        .andExpect(jsonPath("$.image.key").isEmpty());
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldCreateGroupWithImage() throws Exception {
    // when
    var authenticatedUser =
        authFacade.authenticateAndGenerateToken(
            new LoginDto(
                SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
    var result = create(SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES, authenticatedUser);

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(
            jsonPath("$.name").value(SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES.name()))
        .andExpect(jsonPath("$.settledDown").value(false))
        .andExpect(
            jsonPath("$.image.key")
                .value(
                    containsString(
                        SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES
                            .file()
                            .getOriginalFilename())));
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldCreateGroupWithImageAndInvitees() throws Exception {
    // when
    var userCreated = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var authenticatedUser =
        authFacade.authenticateAndGenerateToken(
            new LoginDto(
                SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
    var result =
        create(
            SampleGroups.validGroupWithFileAndInvitees(Set.of(userCreated.id())),
            authenticatedUser);

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value("groupName3"))
        .andExpect(jsonPath("$.settledDown").value(false))
        .andExpect(
            jsonPath("$.image.key")
                .value(
                    containsString(
                        SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES
                            .file()
                            .getOriginalFilename())));
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldReturnBadRequestWhenCreatingGroupWithBlankName() throws Exception {
    // when
    var authenticatedUser =
        authFacade.authenticateAndGenerateToken(
            new LoginDto(
                SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
    var result = create(SampleGroups.INVALID_GROUP, authenticatedUser);

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldReturnBadRequestWhenCreatingGroupWithWrongFileType() throws Exception {
    // when
    var authenticatedUser =
        authFacade.authenticateAndGenerateToken(
            new LoginDto(
                SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
    var result = create(SampleGroups.INVALID_GROUP_FILE, authenticatedUser);

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldReturnBadRequestWhenCreatingGroupWithInviteesIdThatDoesNotExist() throws Exception {
    // when
    var authenticatedUser =
        authFacade.authenticateAndGenerateToken(
            new LoginDto(
                SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
    var result = create(SampleGroups.validGroupWithFileAndInvitees(Set.of("1")), authenticatedUser);

    // then
    result.andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void shouldReturnUnauthorizedWhenCreatingGroupWithoutLogin() throws Exception {
    // when
    var result = create(SampleGroups.VALID_GROUP_WITH_FILE_AND_NO_INVITEES, null);

    // then
    result.andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  @WithMockUser
  void shouldNotDeleteMembersWhenAssociatedWithExpensesOrPayments() throws Exception {
    // when
    var anotherUser = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var group = groupFacade.create(SampleGroups.validGroupWithFileAndInvitees(Set.of(anotherUser.id())),
            registeredUser);
    var invitation = groupFacade.getInvitationByGroupAndInviteeId(group.id(), anotherUser.id());
    groupFacade.updateInvitationStatus(invitation.id(), InvitationStatus.ACCEPTED, anotherUser);
    var members = groupFacade.findAllMembersByGroupId(group.id()).stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();
    var createExpenseDtos =
            Set.of(
                    new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
                    new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()));


    var result = removeMember(group.id(), members.get(0).id());

    result.andExpect(status().isForbidden());

  }

  @Test
  @Transactional
  @WithMockUser
  void shouldDeleteMembersWhenNotAssociatedWithExpensesOrPayments() throws Exception {
    // when
    var anotherUser = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
    var anotherUser2 = userFacade.create(SampleUsers.VALID_USER_3);
    var group = groupFacade.create(SampleGroups.validGroupWithFileAndInvitees(Set.of(anotherUser.id(), anotherUser2.id())),
            registeredUser);
    var invitation = groupFacade.getInvitationByGroupAndInviteeId(group.id(), anotherUser.id());
    var anotherInvitation = groupFacade.getInvitationByGroupAndInviteeId(group.id(), anotherUser2.id());
    groupFacade.updateInvitationStatus(invitation.id(), InvitationStatus.ACCEPTED, anotherUser);
    groupFacade.updateInvitationStatus(anotherInvitation.id(), InvitationStatus.ACCEPTED, anotherUser2);
    var members = groupFacade.findAllMembersByGroupId(group.id()).stream()
            .sorted(Comparator.comparing(MemberDto::id))
            .toList();
    var createExpenseDtos =
            Set.of(
                    new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
                    new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()));


    var result = removeMember(group.id(), members.get(2).id());

    result.andExpect(status().isNoContent());

  }

  private ResultActions getById(String id) throws Exception {
    return mockMvc.perform(get(URL + "/" + id).contentType(MediaType.APPLICATION_JSON));
  }

  private ResultActions create(CreateGroupDto groupDto, AuthDto authDto) throws Exception {
    MockMultipartHttpServletRequestBuilder requestBuilder =
        (MockMultipartHttpServletRequestBuilder)
            multipart(URL).contentType(MediaType.MULTIPART_FORM_DATA);

    if (authDto != null) {
      requestBuilder.header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token());
    }
    if (groupDto.file() != null) {
      requestBuilder.file(
          new MockMultipartFile(
              "file",
              groupDto.file().getOriginalFilename(),
              groupDto.file().getContentType(),
              groupDto.file().getBytes()));
    }

    if (groupDto.name() != null) {
      requestBuilder.param("name", groupDto.name());
    }

    if (groupDto.inviteesId() != null && !groupDto.inviteesId().isEmpty()) {
      String[] inviteeIds =
          groupDto.inviteesId().stream().map(String::valueOf).toArray(String[]::new);
      requestBuilder.param("inviteesId", inviteeIds);
    }

    return mockMvc.perform(requestBuilder);
  }


  private ResultActions removeMember(String groupId, String memberId) throws Exception {
    return mockMvc.perform(delete(URL + "/" + groupId + "/members/" + memberId).contentType(MediaType.APPLICATION_JSON));
  }

}
