package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.storage.InMemoryStorageFacade;
import ug.edu.pl.server.domain.user.UserFacade;

class TestGroupConfiguration {
  InMemoryInvitationRepository invitationRepository = new InMemoryInvitationRepository();
  InMemoryGroupRepository groupRepository = new InMemoryGroupRepository(invitationRepository);

  GroupFacade groupFacade(UserFacade userFacade) {
    return new GroupConfiguration()
        .groupFacade(
            groupRepository,
            new InMemoryGroupRoleRepository(),
            invitationRepository,
            new InMemoryStorageFacade(),
            userFacade);
  }
}
