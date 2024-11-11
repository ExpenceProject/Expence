package ug.edu.pl.server.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ug.edu.pl.server.domain.common.storage.InMemoryStorageFacade;
import ug.edu.pl.server.domain.common.storage.SampleImages;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.domain.user.exception.UserAlreadyExistsException;
import ug.edu.pl.server.domain.user.exception.UserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class UserFacadeTest {

    InMemoryRoleRepository roleRepository = new InMemoryRoleRepository();
    CacheManager cacheManager = new CaffeineCacheManager();
    Cache cache = cacheManager.getCache(UserFacade.CACHE_NAME);
    UserFacade userFacade = new UserFacade(new InMemoryUserRepository(), roleRepository, new UserCreator(), new InMemoryStorageFacade(), cacheManager);

    UserFacadeTest() {
        roleRepository.addRoles();
    }

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
