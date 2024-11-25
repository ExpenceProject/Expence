package ug.edu.pl.server.domain.group;

import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;
import ug.edu.pl.server.domain.user.dto.UserDto;

@Log
@Validated
public class GroupFacade {
  public static final String CACHE_NAME = "groups";
  private final GroupService groupService;

  GroupFacade(GroupService groupService) {
    this.groupService = groupService;
  }

  @Transactional(readOnly = true)
  @Cacheable(value = CACHE_NAME, key = "#id")
  public GroupDto getById(Long id) {
    return groupService.getById(id);
  }

  @Transactional
  public GroupDto create(@Valid CreateGroupDto dto, UserDto currentUser) {
    return groupService.createGroup(dto, currentUser);
  }
}
