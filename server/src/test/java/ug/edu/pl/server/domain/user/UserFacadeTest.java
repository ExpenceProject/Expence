package ug.edu.pl.server.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ug.edu.pl.server.domain.common.exception.DuplicateException;
import ug.edu.pl.server.domain.common.exception.NotFoundException;
import ug.edu.pl.server.domain.common.storage.SampleImages;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserFacadeTest {

    CacheManager cacheManager = new CaffeineCacheManager();
    Cache cache = cacheManager.getCache(UserFacade.CACHE_NAME);
    UserFacade userFacade = new TestUserConfiguration().userFacade(cacheManager);

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
        assertThatThrownBy(() -> userFacade.getByEmail(SampleUsers.INVALID_EMAIL)).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldReturnUserById() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);

        // when
        var userDto = userFacade.getById(Long.valueOf(user.id()));

        // then
        assertThat(userDto).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenAskedForUserWithIdThaDoesNotExist() {
        // when & then
        assertThatThrownBy(() -> userFacade.getById(SampleUsers.ID_THAT_DOES_NOT_EXIST)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenAddingUserWithTheSameEmail() {
        // given
        userFacade.create(SampleUsers.VALID_USER);

        // when & then
        assertThatThrownBy(() -> userFacade.create(SampleUsers.VALID_USER)).isInstanceOf(DuplicateException.class);
    }

    @Test
    void shouldUploadImageAndUpdateCache() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);

        // when
        userFacade.uploadImage(Long.valueOf(user.id()), SampleImages.IMAGE_JPG);

        // then
        var updatedUser = userFacade.getById(Long.valueOf(user.id()));
        assertThat(updatedUser.image()).isNotNull();
        assertThat(updatedUser.image().key()).isNotNull();
        assertThat(getUserDtoFromCache(Long.valueOf(updatedUser.id()))).isEmpty();
        assertThat(getUserDtoFromCache(updatedUser.email())).isEmpty();
    }

    @Test
    void shouldDeleteImageAndEvictCache() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);
        userFacade.uploadImage(Long.valueOf(user.id()), SampleImages.IMAGE_JPG);

        // when
        userFacade.deleteImage(Long.valueOf(user.id()));

        // then
        var updatedUser = userFacade.getById(Long.valueOf(user.id()));
        assertThat(updatedUser.image().key()).isNull();
        assertThat(getUserDtoFromCache(Long.valueOf(updatedUser.id()))).isEmpty();
        assertThat(getUserDtoFromCache(updatedUser.email())).isEmpty();
    }

    @Test
    void shouldUpdateUserAndEvictCache() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);

        // when
        userFacade.update(Long.valueOf(user.id()), SampleUsers.VALID_UPDATE_USER);

        // then
        var updatedUser = userFacade.getById(Long.valueOf(user.id()));
        assertThat(updatedUser.firstName()).isEqualTo(SampleUsers.VALID_UPDATE_USER.firstName());
        assertThat(updatedUser.lastName()).isEqualTo(SampleUsers.VALID_UPDATE_USER.lastName());
        assertThat(updatedUser.phoneNumber()).isEqualTo(SampleUsers.VALID_UPDATE_USER.phoneNumber());
        assertThat(getUserDtoFromCache(Long.valueOf(updatedUser.id()))).isEmpty();
        assertThat(getUserDtoFromCache(updatedUser.email())).isEmpty();
    }

    private Optional<UserDto> getUserDtoFromCache(Long id) {
        return Optional.ofNullable(cache.get(id))
                .map(cachedUser -> (UserDto) cachedUser.get());
    }

    private Optional<UserDto> getUserDtoFromCache(String email) {
        return Optional.ofNullable(cache.get(email))
                .map(cachedUser -> (UserDto) cachedUser.get());
    }
}
