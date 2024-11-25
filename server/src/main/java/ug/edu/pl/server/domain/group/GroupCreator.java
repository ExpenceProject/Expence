package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.group.dto.CreateGroupDto;

class GroupCreator {
  Group from(CreateGroupDto dto) {
    var group = new Group();

    group.setName(dto.name());
    group.setSettledDown(false);
    return group;
  }
}
