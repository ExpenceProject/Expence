package ug.edu.pl.server.domain.user;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.common.persistance.Image;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.user.dto.CreateUserDto;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.domain.user.exception.UserAlreadyExistsException;

import java.util.Set;

@Log
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
    public UserDto create(CreateUserDto dto) {
        // Check for existing user by email; unique constraint in DB mitigates race condition risk
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException("User with email '%s' already exists".formatted(dto.email()));
        }

        var user = userCreator.from(dto);
        var role = roleRepository.findByNameOrThrow(RoleName.ROLE_USER);
        user.setRoles(Set.of(role));

        return userRepository.saveOrThrow(user).dto();
    }

    @Transactional
    public UserDto uploadImage(Long id, MultipartFile file) {
        var user = userRepository.findByIdOrThrow(id);

        if (user.getImage() != null && user.getImage().key() != null) {
            storageFacade.delete(user.getImage().key());
        }

        var imageKey = storageFacade.upload(file);
        user.setImage(new Image(imageKey));
        var userDto = user.dto();
        updateCache(userDto);

        return userDto;
    }

    @Transactional
    public UserDto deleteImage(Long id) {
        var user = userRepository.findByIdOrThrow(id);

        if (user.getImage() != null && user.getImage().key() != null) {
            storageFacade.delete(user.getImage().key());
        }

        user.setImage(new Image(null));
        var userDto = user.dto();
        updateCache(userDto);

        return userDto;
    }

    private void updateCache(UserDto dto) {
        var cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(dto.id(), dto);
            cache.put(dto.email(), dto);
        }
    }
}
