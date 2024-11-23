package ug.edu.pl.server.domain.group;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;
import ug.edu.pl.server.domain.group.dto.GroupDto;

@Log
public class GroupFacade {
    public static final String CACHE_NAME = "groups";
    private final GroupRepository groupRepository;
    private final CreatingGroupHelper creatingGroupHelper;


    GroupFacade(GroupRepository groupRepository, CreatingGroupHelper creatingGroupHelper) {
        this.groupRepository = groupRepository;
        this.creatingGroupHelper = creatingGroupHelper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME, key = "#id")
    public GroupDto getById(Long id){
        return groupRepository.findByIdOrThrow(id).dto();
    }

    @Transactional
    public GroupDto create(CreateGroupDto dto){
        var group = creatingGroupHelper.createGroup(dto);
        return groupRepository.saveOrThrow(group).dto();
    }
}
