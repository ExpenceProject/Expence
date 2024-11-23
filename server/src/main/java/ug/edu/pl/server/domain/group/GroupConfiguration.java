package ug.edu.pl.server.domain.group;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;

@Configuration
class GroupConfiguration {
    @Bean
    GroupFacade groupFacade(GroupRepository groupRepository, GroupRoleRepository groupRoleRepository, StorageFacade storageFacade, CacheManager cacheManager, CurrentUserContext currentUserContext, UserFacade userFacade) {
        CreatingGroupHelper creatingGroupHelper = new CreatingGroupHelper(groupRoleRepository, storageFacade, new GroupCreator(), currentUserContext, userFacade);
        return new GroupFacade(groupRepository, creatingGroupHelper);
    }
}
