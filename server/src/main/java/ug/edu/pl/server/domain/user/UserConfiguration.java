package ug.edu.pl.server.domain.user;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ug.edu.pl.server.domain.common.storage.StorageFacade;

@Configuration
class UserConfiguration {

    @Bean
    UserFacade userFacade(UserRepository userRepository, RoleRepository roleRepository, StorageFacade storageFacade, CacheManager cacheManager) {
        return new UserFacade(userRepository, roleRepository, new UserCreator(), storageFacade, cacheManager);
    }
}
