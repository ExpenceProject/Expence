package ug.edu.pl.server.domain.user;

import org.springframework.cache.CacheManager;
import ug.edu.pl.server.domain.common.storage.InMemoryStorageFacade;

class TestUserConfiguration {

    UserFacade userFacade(CacheManager cacheManager) {
        return new UserConfiguration().userFacade(new InMemoryUserRepository(), new InMemoryRoleRepository(), new InMemoryStorageFacade(), cacheManager);
    }
}
