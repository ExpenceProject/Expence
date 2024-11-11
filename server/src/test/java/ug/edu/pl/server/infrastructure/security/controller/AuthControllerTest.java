package ug.edu.pl.server.infrastructure.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.user.SampleUsers;
import ug.edu.pl.server.domain.user.dto.CreateUserDto;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends IntegrationTest {

    private static final String URL = "/api/auth";

    @Autowired
    private AuthFacade authFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void shouldRegisterAndLogin() throws Exception {
        // when
        var result = register(SampleUsers.VALID_USER);

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(SampleUsers.VALID_USER.email()))
                .andExpect(jsonPath("$.firstName").value(SampleUsers.VALID_USER.firstName()))
                .andExpect(jsonPath("$.lastName").value(SampleUsers.VALID_USER.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(SampleUsers.VALID_USER.phoneNumber()));

        // when
        var responseBody = result.andReturn().getResponse().getContentAsString();
        var createdUser = objectMapper.readValue(responseBody, UserDto.class);
        var loginResult = login(new LoginDto(createdUser.email(), SampleUsers.VALID_USER.password()));

        // then
        loginResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user").isNotEmpty());

        var loggedInUser = objectMapper.readValue(loginResult.andReturn().getResponse().getContentAsString(), AuthDto.class).user();
        assertThat(createdUser.id()).isEqualTo(loggedInUser.id());
        assertThat(loggedInUser.email()).isEqualTo(createdUser.email());
        assertThat(loggedInUser.firstName()).isEqualTo(createdUser.firstName());
        assertThat(loggedInUser.lastName()).isEqualTo(createdUser.lastName());
        assertThat(loggedInUser.phoneNumber()).isEqualTo(createdUser.phoneNumber());
    }

    @Test
    @Transactional
    void shouldReturnConflictWhenCreatingUserWithTheSameEmail() throws Exception {
        // given
        authFacade.register(SampleUsers.VALID_USER);

        // when
        var result = register(SampleUsers.VALID_USER);

        // then
        result.andExpect(status().isConflict());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithInvalidPassword() throws Exception {
        // when
        var result = register(SampleUsers.USER_WITH_INVALID_PASSWORD);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankPassword() throws Exception {
        // when
        var result = register(SampleUsers.USER_WITH_BLANK_PASSWORD);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithInvalidEmail() throws Exception {
        // when
        var result = register(SampleUsers.USER_WITH_INVALID_EMAIL);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankEmail() throws Exception {
        // when
        var result = register(SampleUsers.USER_WITH_BLANK_EMAIL);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankFirstName() throws Exception {
        // when
        var result = register(SampleUsers.USER_WITH_BLANK_FIRST_NAME);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankLastName() throws Exception {
        // when
        var result = register(SampleUsers.USER_WITH_BLANK_LAST_NAME);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenLoginWithWrongCredentials() throws Exception {
        // when
        var result = login(new LoginDto(SampleUsers.VALID_USER.email(), SampleUsers.VALID_USER.password()));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenLoginWithWrongEmail() throws Exception {
        // given
        authFacade.register(SampleUsers.VALID_USER);

        // when
        var result = login(new LoginDto(SampleUsers.EMAIL_THAT_DOES_NOT_EXIST, SampleUsers.VALID_USER.password()));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenLoginWithWrongPassword() throws Exception {
        // given
        authFacade.register(SampleUsers.VALID_USER);

        // when
        var result = login(new LoginDto(SampleUsers.VALID_USER.email(), SampleUsers.PASSWORD_THAT_DOES_NOT_EXIST));

        // then
        result.andExpect(status().isUnauthorized());
    }

    private ResultActions register(CreateUserDto createUserDto) throws Exception {
        return mockMvc.perform(post(URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)));
    }

    private ResultActions login(LoginDto loginDto) throws Exception {
        return mockMvc.perform(post(URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));
    }
}
