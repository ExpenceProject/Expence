package ug.edu.pl.server.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ug.edu.pl.server.domain.common.persistance.InMemoryRepository;
import ug.edu.pl.server.domain.user.exception.SavingUserException;
import ug.edu.pl.server.domain.user.exception.UserNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

interface UserRepository extends Repository<User, Long> {

    User save(User user);

    Boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    default User saveOrThrow(User user) {
        try {
            return save(user);
        } catch (Exception ex) {
            throw new SavingUserException(ex.getMessage());
        }
    }
}

class InMemoryUserRepository implements UserRepository, InMemoryRepository<User> {

    private final Map<Long, User> userIdMap = new ConcurrentHashMap<>();
    private final Map<String, User> userEmailMap = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        updateTimestampsAndVersion(user);
        userIdMap.put(user.getId(), user);
        userEmailMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userEmailMap.containsKey(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userIdMap.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userEmailMap.get(email));
    }
}
