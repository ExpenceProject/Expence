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
        var userDto = userFacade.getById(user.id());

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
        var updatedUser = userFacade.uploadImage(user.id(), SampleImages.IMAGE_JPG);

        // then
        assertThat(updatedUser.image()).isNotNull();
        assertThat(updatedUser.image().key()).isNotNull();
        assertThat(getUserDtoFromCache(updatedUser.id())).isEqualTo(updatedUser);
        assertThat(getUserDtoFromCache(updatedUser.email())).isEqualTo(updatedUser);
    }

    @Test
    void shouldDeleteImageAndUpdateCache() {
        // given
        var user = userFacade.create(SampleUsers.VALID_USER);
        userFacade.uploadImage(user.id(), SampleImages.IMAGE_JPG);

        // when
        var updatedUser = userFacade.deleteImage(user.id());

        // then
        assertThat(updatedUser.image().key()).isNull();
        assertThat(getUserDtoFromCache(updatedUser.id())).isEqualTo(updatedUser);
        assertThat(getUserDtoFromCache(updatedUser.email())).isEqualTo(updatedUser);
    }

    private UserDto getUserDtoFromCache(Long id) {
        var cachedUser = cache.get(id);
        assert cachedUser != null;
        return (UserDto) cachedUser.get();
    }

    private UserDto getUserDtoFromCache(String email) {
        var cachedUser = cache.get(email);
        assert cachedUser != null;
        return (UserDto) cachedUser.get();
    }
}
