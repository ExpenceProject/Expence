package ug.edu.pl.server.infrastructure.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.base.IntegrationTest;
import ug.edu.pl.server.domain.common.storage.SampleImages;
import ug.edu.pl.server.domain.user.SampleUsers;
import ug.edu.pl.server.domain.user.dto.UpdateUserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.SampleRegisterUsers;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends IntegrationTest {

    static final String URL = "/api/users";

    @Autowired
    AuthFacade authFacade;

    @Test
    @Transactional
    @WithMockUser
    void shouldGetAllUsers() throws Exception {
        // given
        var registeredUser1 = authFacade.register(SampleRegisterUsers.VALID_USER);
        var registeredUser2 = authFacade.register(SampleRegisterUsers.VALID_USER_2);

        // when
        var result = mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder(
                        registeredUser1.id(),
                        registeredUser2.id()
                )));
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenGetAllWithoutLogin() throws Exception {
        // when
        var result = mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldGetById() throws Exception {
        // given
        var registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);

        // when
        var result = getById(Long.valueOf(registeredUser.id()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(registeredUser.email()))
                .andExpect(jsonPath("$.id").value(registeredUser.id()))
                .andExpect(jsonPath("$.firstName").value(registeredUser.firstName()))
                .andExpect(jsonPath("$.lastName").value(registeredUser.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(registeredUser.phoneNumber()));
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenGetByIdWithoutLogin() throws Exception {
        // given
        var registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);

        // when
        var result = getById(Long.valueOf(registeredUser.id()));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void shouldReturnNotFoundWhenGetByIdThatDoesNotExist() throws Exception {
        // when
        var result = getById(SampleRegisterUsers.ID_THAT_DOES_NOT_EXIST);

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void shouldUploadAndDeleteImage() throws Exception {
        // given
        var registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));

        // when
        var result = uploadImageWithToken(Long.valueOf(registeredUser.id()), authenticatedUser, (MockMultipartFile) SampleImages.IMAGE_JPG);

        // then
        result.andExpect(status().isOk());

        // when
        var deleteResult = deleteImageWithToken(Long.valueOf(registeredUser.id()), authenticatedUser);

        // then
        deleteResult.andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldReturnForbiddenWhenAuthenticatedUserTryToUploadNotHisImage() throws Exception {
        // given
        authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
        var registeredUser2 = authFacade.register(SampleRegisterUsers.VALID_USER_2);

        // when
        var result = uploadImageWithToken(Long.valueOf(registeredUser2.id()), authenticatedUser, (MockMultipartFile) SampleImages.IMAGE_JPG);

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenTryToUploadImageWithoutAuthentication() throws Exception {
        // when
        var result = mockMvc.perform(multipart(URL + "/image/" + SampleRegisterUsers.ID_THAT_DOES_NOT_EXIST)
                .file((MockMultipartFile) SampleImages.IMAGE_JPG)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnForbiddenWhenAuthenticatedUserTryToDeleteNotHisImage() throws Exception {
        // given
        authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
        var registeredUser2 = authFacade.register(SampleRegisterUsers.VALID_USER_2);

        // when
        var result = deleteImageWithToken(Long.valueOf(registeredUser2.id()), authenticatedUser);

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenTryToDeleteImageWithoutAuthentication() throws Exception {
        // when
        var result = mockMvc.perform(delete(URL + "/image/" + SampleRegisterUsers.ID_THAT_DOES_NOT_EXIST).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenUploadingInvalidFile() throws Exception {
        // given
        var registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));

        // when
        var result = uploadImageWithToken(Long.valueOf(registeredUser.id()), authenticatedUser, (MockMultipartFile) SampleImages.IMAGE_GIF);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldUpdate() throws Exception {
        // given
        var registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));

        // when
        var result = updateWithToken(Long.valueOf(registeredUser.id()), authenticatedUser, SampleUsers.VALID_UPDATE_USER);

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldReturnBadRequestWhenUpdatingWithInvalidData() throws Exception {
        // given
        var registeredUser = authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));

        // when
        var result = updateWithToken(Long.valueOf(registeredUser.id()), authenticatedUser, UpdateUserDto.builder().build());

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void shouldReturnUnauthorizedWhenTryToUpdateWithoutAuthentication() throws Exception {
        // when
        var result = mockMvc.perform(put(URL + "/" + SampleRegisterUsers.ID_THAT_DOES_NOT_EXIST).contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void shouldReturnForbiddenWhenAuthenticatedUserTryToUpdateNotHisProfile() throws Exception {
        // given
        authFacade.register(SampleRegisterUsers.VALID_USER);
        var authenticatedUser = authFacade.authenticateAndGenerateToken(new LoginDto(SampleRegisterUsers.VALID_USER.email(), SampleRegisterUsers.VALID_USER.password()));
        var registeredUser2 = authFacade.register(SampleRegisterUsers.VALID_USER_2);

        // when
        var result = updateWithToken(Long.valueOf(registeredUser2.id()), authenticatedUser, SampleUsers.VALID_UPDATE_USER);

        // then
        result.andExpect(status().isForbidden());
    }

    private ResultActions getById(Long id) throws Exception {
        return mockMvc.perform(get(URL + "/" + id).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions uploadImageWithToken(Long id, AuthDto authDto, MockMultipartFile image) throws Exception {
        return mockMvc.perform(multipart(URL + "/image/" + id)
                .file(image)
                .header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token())
                .contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions deleteImageWithToken(Long id, AuthDto authDto) throws Exception {
        return mockMvc.perform(delete(URL + "/image/" + id)
                .header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token())
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions updateWithToken(Long id, AuthDto authDto, UpdateUserDto dto) throws Exception {
        return mockMvc.perform(put(URL + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, authDto.tokenType() + " " + authDto.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }
}
