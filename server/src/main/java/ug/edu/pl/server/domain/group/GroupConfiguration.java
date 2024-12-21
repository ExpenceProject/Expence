package ug.edu.pl.server.domain.group;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ug.edu.pl.server.domain.common.storage.StorageFacade;
import ug.edu.pl.server.domain.user.UserFacade;

@Configuration
class GroupConfiguration {
  @Bean
  GroupFacade groupFacade(
      GroupRepository groupRepository,
      GroupRoleRepository groupRoleRepository,
      InvitationRepository invitationRepository,
      StorageFacade storageFacade,
      UserFacade userFacade) {
    GroupService groupService =
        new GroupService(
            groupRepository, groupRoleRepository, storageFacade, new GroupCreator(), userFacade);
    InvitationService invitationService = new InvitationService(invitationRepository);
    return new GroupFacade(groupService, invitationService);
  }
}
