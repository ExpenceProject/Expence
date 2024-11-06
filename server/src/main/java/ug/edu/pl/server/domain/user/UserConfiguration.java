package ug.edu.pl.server.domain.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserConfiguration {

    UserFacade userFacade() {
        var inMemoryRoleRepository = new InMemoryRoleRepository();

        // Add default roles to the in-memory repository for testing purposes
        inMemoryRoleRepository.addRoles();

        return userFacade(new InMemoryUserRepository(), inMemoryRoleRepository);
    }

    @Bean
    UserFacade userFacade(UserRepository userRepository, RoleRepository roleRepository) {
        return new UserFacade(userRepository, roleRepository, new UserCreator());
    }
}
