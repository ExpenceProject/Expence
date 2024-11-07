package ug.edu.pl.server.domain.user;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.user.dto.CreateUserDto;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.domain.user.exception.UserAlreadyExistsException;

import java.util.Set;

@Log
public class UserFacade {

    private static final String CACHE_NAME = "users";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCreator userCreator;

    UserFacade(UserRepository userRepository, RoleRepository roleRepository, UserCreator userCreator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userCreator = userCreator;
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
}
