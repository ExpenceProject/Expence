package ug.edu.pl.server.domain.group;

import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.group.dto.*;
import ug.edu.pl.server.domain.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

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

  @Transactional
  public GroupDto updateGroup(Long id, UpdateGroupDto dto) {
    return groupService.updateGroup(id, dto);
  }

  @Transactional
  public void deleteGroup(Long id) {
    groupService.deleteGroup(id);
  }

  @Transactional
  public void deleteMember(Long groupId, Long memberId) {
    groupService.deleteMember(groupId, memberId);
  }

  @Transactional
  public MemberDto updateMemberRole(Long groupId, Long memberId, String role) {
    return groupService.updateMemberRole(groupId, memberId, role);
  }

  @Transactional
  public MemberDto updateMemberNickname(Long groupId, Long memberId, String nickname) {
    return groupService.updateMemberNickname(groupId, memberId, nickname);
  }

  @Transactional
  public Collection<GroupDto> findAllGroupsByUserId(Long userId) {
    return groupService.findAllGroupsByUserId(userId);
  }

  @Transactional
  public Collection<MemberDto> findAllMembersByGroupId(Long groupId) {
    return groupService.findAllMembersByGroupId(groupId);
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
  public PaymentDto createPayment(@Valid CreatePaymentDto dto) {
    return billService.createPayment(dto);
  }

  @Transactional
  public PaymentDto getPaymentById(Long id) {
    return billService.getPaymentById(id);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsBySenderIdAndGroupId(Long senderId, Long groupId) {
    return billService.getPaymentsBySenderIdAndGroupId(senderId, groupId);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsByReceiverIdAndGroupId(Long receiverId, Long groupId) {
    return billService.getPaymentsByReceiverIdAndGroupId(receiverId, groupId);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsByGroupId(Long groupId) {
    return billService.getPaymentsByGroupId(groupId);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsByGroupIdAndSenderIdAndReceiverId(Long groupId, Long senderId, Long receiverId) {
    return billService.getPaymentsByGroupIdAndSenderIdAndReceiverId(groupId, senderId, receiverId);
  }

  @Transactional
  public Void deletePayment(Long id) {
    return billService.deletePayment(id);
  }

  @Transactional
  public List<InvitationDto> createInvitations(@Valid CreateInvitationsDto dto) {
    return invitationService.create(dto);
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
  public Collection<InvitationDto> getInvitationsByGroupId(Long groupId, InvitationStatus status) {
    return invitationService.getByGroupId(groupId, status);
  }

  @Transactional
  public Collection<InvitationDto> getInvitationsByInviteeId(Long inviteeId, InvitationStatus status) {
    return invitationService.getByInviteeId(inviteeId, status);
  }

  @Transactional
  public void updateInvitationStatus(Long id, InvitationStatus invitationStatus, UserDto currentUser) {
      invitationService.updateInvitationStatus(id, invitationStatus, currentUser);
  }
}
