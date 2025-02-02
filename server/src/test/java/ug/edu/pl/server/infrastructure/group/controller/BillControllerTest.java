package ug.edu.pl.server.infrastructure.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.InvitationStatus;
import ug.edu.pl.server.domain.group.SampleGroups;
import ug.edu.pl.server.domain.group.dto.*;
import ug.edu.pl.server.domain.user.SampleUsers;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.SampleRegisterUsers;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillControllerTest extends IntegrationTest {
    static final String URL = "/api/bills";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired GroupFacade groupFacade;

    @Autowired
    UserFacade userFacade;

    @Autowired
    AuthFacade authFacade;

    private UserDto registeredUser;

    private GroupDto group;

    private UserDto anotherUser;

    private InvitationDto invitation;

    private List<MemberDto> members;

    Set<CreateExpenseDto> createExpenseDtos = new HashSet<>();

    @BeforeEach
    void setUp() {
        registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);
        anotherUser = userFacade.create(SampleUsers.ANOTHER_VALID_USER);
        group = groupFacade.create(SampleGroups.validGroupWithFileAndInvitees(Set.of(anotherUser.id())),
                registeredUser);
        invitation = groupFacade.getInvitationByGroupAndInviteeId(group.id(), anotherUser.id());
        groupFacade.updateInvitationStatus(invitation.id(), InvitationStatus.ACCEPTED, anotherUser);
        members = groupFacade.findAllMembersByGroupId(group.id()).stream()
                .sorted(Comparator.comparing(MemberDto::id))
                .toList();
        createExpenseDtos =
                Set.of(
                        new CreateExpenseDto(members.get(1).id(), new BigDecimal(100)),
                        new CreateExpenseDto(members.get(0).id(), new BigDecimal(50)));

    }

    @Test
    @Transactional
    @WithMockUser
    void shouldGetById() throws Exception {
        // given
        var createdBill =
                groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()));

        //when
        var result = getById(createdBill.id());

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdBill.id()))
                .andExpect(jsonPath("$.name").value(createdBill.name()))
                .andExpect(jsonPath("$.totalAmount").value(createdBill.totalAmount()))
                .andExpect(jsonPath("$.lender").isNotEmpty())
                .andExpect(jsonPath("$.expenses").isNotEmpty());

    }

    @Test
    @Transactional
    @WithMockUser
    void shouldGetAllByGroupId() throws Exception {
        // given
        var createdBill =
                groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()));

        //when
        var result = getAllByGroupId(group.id());

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdBill.id()))
                .andExpect(jsonPath("$[0].name").value(createdBill.name()))
                .andExpect(jsonPath("$[0].totalAmount").value(createdBill.totalAmount()))
                .andExpect(jsonPath("$[0].lender").isNotEmpty())
                .andExpect(jsonPath("$[0].expenses").isNotEmpty());

    }

    @Test
    @Transactional
    @WithMockUser
    void shouldGetAllByUserIdAndGroupId() throws Exception {
        // given
        var createdBill =
                groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()));

        //when
        var result = getAllByUserIdAndGroupId(registeredUser.id(), group.id());

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdBill.id()))
                .andExpect(jsonPath("$[0].name").value(createdBill.name()))
                .andExpect(jsonPath("$[0].totalAmount").value(createdBill.totalAmount()))
                .andExpect(jsonPath("$[0].lender").isNotEmpty())
                .andExpect(jsonPath("$[0].expenses").isNotEmpty());

    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenGetByIdWithoutLogin() throws Exception {
        // given
        var createdBill =
                groupFacade.createBill(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()));

        // when
        var result = getById(createdBill.id());

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldReturnNotFoundWhenGetByIdThatDoesNotExist() throws Exception {
        // when
        var result = getById("1");

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldCreateGroup() throws Exception {
        // when
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
        var result =
                create(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()), authenticatedUser);

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("group"))
                .andExpect(jsonPath("$.totalAmount").value(150L))
                .andExpect(jsonPath("$.lender").isNotEmpty())
                .andExpect(jsonPath("$.expenses").isNotEmpty());
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldUpdateBill() throws Exception {
        // given
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
        var createResult =
                create(new CreateBillDto("group", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()), authenticatedUser);
        var createdBill = parseResponse(createResult, BillDto.class);

        Set<CreateExpenseDto> updatedExpenses = Set.of(
                new CreateExpenseDto(members.get(1).id(), new BigDecimal(200)),
                new CreateExpenseDto(members.get(0).id(), new BigDecimal(100))
        );

    var updateBillDto =
        new CreateBillDto(
            "updatedGroup",
            updatedExpenses,
            new BigDecimal(300),
            members.get(1).id(),
            group.id());

        // when
        var result =
                update(createdBill.id(), updateBillDto, authenticatedUser);

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedGroup"))
                .andExpect(jsonPath("$.totalAmount").value(300L))
                .andExpect(jsonPath("$.lender.id").value(members.get(1).id()))
                .andExpect(jsonPath("$.expenses").isNotEmpty())
                .andExpect(jsonPath("$.expenses[?(@.amount == 200)]").isNotEmpty())
                .andExpect(jsonPath("$.expenses[?(@.amount == 100)]").isNotEmpty());
    }


    @Test
    @Transactional
    @WithMockUser
    void shouldReturnBadRequestWhenCreatingBillWithBlankName() throws Exception {
        // when
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
        var result =
                create(new CreateBillDto("", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()), authenticatedUser);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenCreatingBillWithoutLogin() throws Exception {
        // when
        var result =
                create(new CreateBillDto("", createExpenseDtos, new BigDecimal(150), members.get(0).id(), group.id()), null);

        // then
        result.andExpect(status().isUnauthorized());
    }

    private ResultActions getById(String id) throws Exception {
        return mockMvc.perform(get(URL + "/" + id).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions getAllByGroupId(String id) throws Exception {
        return mockMvc.perform(get(URL + "/group/" + id).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions getAllByUserIdAndGroupId(String userId, String groupId) throws Exception {
        return mockMvc.perform(get(URL + "/user/" + userId + "/group/" + groupId).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions create(CreateBillDto billDto, AuthDto authDto) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(billDto));

        if (authDto != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token());
        }

        return mockMvc.perform(requestBuilder);

    }

    private <T> T parseResponse(ResultActions resultActions, Class<T> responseType) throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, responseType);
    }

    private ResultActions update(String billId, CreateBillDto updateBillDto, AuthDto authDto) throws Exception {
        return mockMvc.perform(put("/api/bills/" + billId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token())
                .content(objectMapper.writeValueAsString(updateBillDto)));
    }

}
