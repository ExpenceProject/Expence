package ug.edu.pl.server.domain.user;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import ug.edu.pl.server.domain.common.storage.InMemoryStorageFacade;

public class TestUserConfiguration {

    UserFacade userFacade(CacheManager cacheManager) {
        return new UserConfiguration().userFacade(new InMemoryUserRepository(), new InMemoryRoleRepository(), new InMemoryStorageFacade(), cacheManager);
    }

    public UserFacade userFacadeForGroup() {
        CacheManager cacheManager = new CaffeineCacheManager();
        return new UserConfiguration().userFacade(new InMemoryUserRepository(), new InMemoryRoleRepository(), new InMemoryStorageFacade(), cacheManager);
    }
}
