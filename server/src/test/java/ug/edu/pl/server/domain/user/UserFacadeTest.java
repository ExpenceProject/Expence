package ug.edu.pl.server.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ug.edu.pl.server.domain.user.exception.UserAlreadyExistsException;
import ug.edu.pl.server.domain.user.exception.UserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class UserFacadeTest {

    UserFacade userFacade = new UserConfiguration().userFacade();

    @Test
    void shouldReturnUserByEmail() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);

        // when
        var userDto = userFacade.getByEmail(user.email());

        // then
        assertThat(userDto).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenAskedForUserWithEmailThaDoesNotExist() {
        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userFacade.getByEmail(SampleUsers.INVALID_EMAIL));
    }

    @Test
    void shouldReturnUserById() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);

        // when
        var userDto = userFacade.getById(user.id());

        // then
        assertThat(userDto).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenAskedForUserWithIdThaDoesNotExist() {
        // when & then
        assertThrows(UserNotFoundException.class, () -> userFacade.getById(SampleUsers.ID_THAT_DOES_NOT_EXIST));
    }

    @Test
    void shouldThrowExceptionWhenAddingUserWithTheSameEmail() {
        // given
        userFacade.create(SampleUsers.VALID_USER);

        // when & then
        assertThrows(UserAlreadyExistsException.class, () -> userFacade.create(SampleUsers.VALID_USER));
    }
}
