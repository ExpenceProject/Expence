package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.storage.InMemoryStorageFacade;
import ug.edu.pl.server.domain.user.UserFacade;

class TestGroupConfiguration {
  GroupFacade groupFacade(UserFacade userFacade) {
    return new GroupConfiguration()
        .groupFacade(
                new InMemoryGroupRepository(),
                new InMemoryGroupRoleRepository(),
                new InMemoryInvitationRepository(),
                new InMemoryStorageFacade(),
                userFacade,
                new InMemoryBillRepository(),
                new InMemoryPaymentRepository(),
                new InMemoryMemberRepository());
  }
}
