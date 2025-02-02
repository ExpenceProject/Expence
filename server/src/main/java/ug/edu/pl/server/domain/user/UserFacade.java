package ug.edu.pl.server.domain.user;

import jakarta.validation.Valid;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.common.exception.DuplicateException;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.common.validation.image.ValidImage;
import ug.edu.pl.server.domain.user.dto.CreateUserDto;
import ug.edu.pl.server.domain.user.dto.UpdateUserDto;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.List;
import java.util.Set;

@Log
@Validated
public class UserFacade {

    public static final String CACHE_NAME = "users";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCreator userCreator;
    private final StorageFacade storageFacade;
    private final CacheManager cacheManager;

    UserFacade(UserRepository userRepository, RoleRepository roleRepository, UserCreator userCreator, StorageFacade storageFacade, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userCreator = userCreator;
        this.storageFacade = storageFacade;
        this.cacheManager = cacheManager;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(User::dto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME, key = "#id")
    public UserDto getById(Long id) {
        return userRepository.findByIdOrThrow(id).dto();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME, key = "#email")
    public UserDto getByEmail(String email) {
        return userRepository.findByEmailOrThrow(email).dto();
    }

    @Transactional
    public UserDto create(@Valid CreateUserDto dto) {
        // Check for existing user by email; unique constraint in DB mitigates race condition risk
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicateException("User with email '%s' already exists".formatted(dto.email()));
        }

        var user = userCreator.from(dto);
        var role = roleRepository.findByNameOrThrow(RoleName.ROLE_USER);
        user.setRoles(Set.of(role));

        return userRepository.saveOrThrow(user).dto();
    }

    @Transactional
    public void uploadImage(Long id, @ValidImage MultipartFile file) {
        var user = userRepository.findByIdOrThrow(id);

        if (user.getImage() != null && user.getImage().key() != null) {
            storageFacade.delete(user.getImage().key());
        }

        var imageKey = storageFacade.upload(file);
        user.setImage(new Image(imageKey));
        evictCache(user);
    }

    @Transactional
    public void deleteImage(Long id) {
        var user = userRepository.findByIdOrThrow(id);

        if (user.getImage() != null && user.getImage().key() != null) {
            storageFacade.delete(user.getImage().key());
        }

        user.setImage(new Image(null));
        evictCache(user);
    }

    @Transactional
    public void update(Long id, @Valid UpdateUserDto dto) {
        var user = userRepository.findByIdOrThrow(id);
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPhoneNumber(dto.phoneNumber());
        evictCache(user);
    }

    private void evictCache(User user) {
        var cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.evict(user.getId());
            cache.evict(user.getEmail());
        }
    }
}
