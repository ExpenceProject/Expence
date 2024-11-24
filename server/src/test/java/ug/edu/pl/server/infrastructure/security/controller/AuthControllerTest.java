package ug.edu.pl.server.infrastructure.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.SampleRegisterUsers;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.RegisterUserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends IntegrationTest {

    static final String URL = "/api/auth";

    @Autowired
    AuthFacade authFacade;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Transactional
    void shouldRegisterAndLogin() throws Exception {
        // when
        var result = register(SampleRegisterUsers.VALID_USER);

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(SampleRegisterUsers.VALID_USER.email()))
                .andExpect(jsonPath("$.firstName").value(SampleRegisterUsers.VALID_USER.firstName()))
                .andExpect(jsonPath("$.lastName").value(SampleRegisterUsers.VALID_USER.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(SampleRegisterUsers.VALID_USER.phoneNumber()));

        // when
        var createdUser = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), UserDto.class);
        var loginResult = login(new LoginDto(createdUser.email(), SampleRegisterUsers.VALID_USER.password()));

        // then
        loginResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user").isNotEmpty());

        var loggedInUser = objectMapper.readValue(loginResult.andReturn().getResponse().getContentAsString(), AuthDto.class).user();
        assertThat(loggedInUser.id()).isEqualTo(createdUser.id());
        assertThat(loggedInUser.email()).isEqualTo(createdUser.email());
        assertThat(loggedInUser.firstName()).isEqualTo(createdUser.firstName());
        assertThat(loggedInUser.lastName()).isEqualTo(createdUser.lastName());
        assertThat(loggedInUser.phoneNumber()).isEqualTo(createdUser.phoneNumber());
    }

    @Test
    @Transactional
    void shouldReturnConflictWhenCreatingUserWithTheSameEmail() throws Exception {
        // given
        authFacade.register(SampleRegisterUsers.VALID_USER);

        // when
        var result = register(SampleRegisterUsers.VALID_USER);

        // then
        result.andExpect(status().isConflict());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithInvalidPassword() throws Exception {
        // when
        var result = register(SampleRegisterUsers.USER_WITH_INVALID_PASSWORD);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankPassword() throws Exception {
        // when
        var result = register(SampleRegisterUsers.USER_WITH_BLANK_PASSWORD);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithInvalidEmail() throws Exception {
        // when
        var result = register(SampleRegisterUsers.USER_WITH_INVALID_EMAIL);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankEmail() throws Exception {
        // when
        var result = register(SampleRegisterUsers.USER_WITH_BLANK_EMAIL);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankFirstName() throws Exception {
        // when
        var result = register(SampleRegisterUsers.USER_WITH_BLANK_FIRST_NAME);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenCreatingUserWithBlankLastName() throws Exception {
        // when
        var result = register(SampleRegisterUsers.USER_WITH_BLANK_LAST_NAME);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenLoginWithWrongCredentials() throws Exception {
        // when
        var result = login(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenLoginWithWrongEmail() throws Exception {
        // given
        authFacade.register(SampleRegisterUsers.VALID_USER);

        // when
        var result = login(new LoginDto(SampleRegisterUsers.EMAIL_THAT_DOES_NOT_EXIST, SampleRegisterUsers.VALID_USER.password()));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenLoginWithWrongPassword() throws Exception {
        // given
        authFacade.register(SampleRegisterUsers.VALID_USER);

        // when
        var result = login(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.PASSWORD_THAT_DOES_NOT_EXIST));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnCurrentUser() throws Exception {
        // given
        var createdUser = authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));

        // when
        var result = me(authenticatedUser);

        // then
        result.andExpect(status().isOk());

        var loggedInUser = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), UserDto.class);
        assertThat(loggedInUser.id()).isEqualTo(createdUser.id());
        assertThat(loggedInUser.email()).isEqualTo(createdUser.email());
        assertThat(loggedInUser.firstName()).isEqualTo(createdUser.firstName());
        assertThat(loggedInUser.lastName()).isEqualTo(createdUser.lastName());
        assertThat(loggedInUser.phoneNumber()).isEqualTo(createdUser.phoneNumber());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenGettingCurrentUserWithoutAuthentication() throws Exception {
        // when
        var result = mockMvc.perform(get(URL + "/me").contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized());
    }

    private ResultActions register(RegisterUserDto dto) throws Exception {
        return mockMvc.perform(post(URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    private ResultActions login(LoginDto loginDto) throws Exception {
        return mockMvc.perform(post(URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));
    }

    private ResultActions me(AuthDto authDto) throws Exception {
        return mockMvc.perform(get(URL + "/me")
                .header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token())
                .contentType(MediaType.APPLICATION_JSON));
    }
}
