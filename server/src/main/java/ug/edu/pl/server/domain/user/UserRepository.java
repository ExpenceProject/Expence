package ug.edu.pl.server.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ug.edu.pl.server.domain.user.exception.SavingUserException;
import ug.edu.pl.server.domain.user.exception.UserNotFoundException;

import java.util.Optional;

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
