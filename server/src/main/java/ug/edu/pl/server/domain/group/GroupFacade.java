package ug.edu.pl.server.domain.group;

import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.group.dto.*;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.Collection;

@Log
@Validated
public class GroupFacade {
  public static final String CACHE_NAME = "groups";
  private final GroupService groupService;
  private final BillService billService;
  private final InvitationService invitationService;

  GroupFacade(GroupService groupService, InvitationService invitationService, BillService billService) {
    this.groupService = groupService;
    this.invitationService = invitationService;
    this.billService = billService;
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

  @Transactional(readOnly = true)
  @Cacheable(value = CACHE_NAME, key = "#id")
  public BillDto getBillById(Long id) {
    return billService.getById(id);
  }

  @Transactional
  public BillDto createBill(@Valid CreateBillDto dto) {
    return billService.create(dto);
  }

  @Transactional
  public InvitationDto getInvitationById(Long id) {
    return invitationService.getById(id);
  }

  @Transactional
  public InvitationDto getInvitationByGroupAndInviteeId(Long groupId, Long inviteeId) {
    return invitationService.getByGroupAndInviteeId(groupId, inviteeId);
  }

  @Transactional
  public Collection<InvitationDto> getInvitationsByGroupId(Long groupId) {
    return invitationService.getByGroupId(groupId);
  }

  @Transactional
  public Collection<InvitationDto> getInvitationsByInviteeId(Long inviteeId) {
    return invitationService.getByInviteeId(inviteeId);
  }

  @Transactional
  public void updateInvitationStatus(Long id, InvitationStatus invitationStatus) {
    invitationService.updateInvitationStatus(id, invitationStatus);
  }
}
