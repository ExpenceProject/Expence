package ug.edu.pl.server.domain.group;

import jakarta.validation.Valid;
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
  public GroupDto getById(String id) {
    return groupService.getById(id);
  }

  @Transactional
  public GroupDto create(@Valid CreateGroupDto dto, UserDto currentUser) {
    return groupService.createGroup(dto, currentUser);
  }

  @Transactional
  public GroupDto updateGroup(String id, UpdateGroupDto dto, String userId) {
    return groupService.updateGroup(id, dto, userId);
  }

  @Transactional
  public GroupDto updateGroupSettledDown(String id, String userId) {
    return groupService.updateGroupSettledDown(id, userId);
  }

  @Transactional
  public void deleteGroup(String id, String userId) {
    groupService.deleteGroup(id, userId);
  }

  @Transactional
  public Collection<GroupDto> findAllGroupsByUserId(String userId) {
    return groupService.findAllGroupsByUserId(userId);
  }

  @Transactional
  public Collection<MemberBalanceDto> getMemberBalance(Long memberId) {
    return groupService.getMemberBalance(memberId);
  }

  @Transactional
  public String getUserIdByMemberIdAndGroupId(String memberId, String groupId) {
    return groupService.getUserIdFromMember(memberId, groupId);
  }

  @Transactional
  public MemberDto updateMemberRole(String groupId, String memberId, String role, String userId) {
    return groupService.updateMemberRole(groupId, memberId, role, userId);
  }

  @Transactional
  public MemberDto updateMemberNickname(String groupId, String memberId, String nickname) {
    return groupService.updateMemberNickname(groupId, memberId, nickname);
  }

  @Transactional
  public Collection<MemberDto> findAllMembersByGroupId(String groupId) {
    return groupService.findAllMembersByGroupId(groupId);
  }

  @Transactional
  public void deleteMember(String groupId, String memberId, String userId) {
    groupService.deleteMember(groupId, memberId, userId);
  }

  @Transactional(readOnly = true)
  public BillDto getBillById(String id) {
    return billService.getById(id);
  }

  @Transactional
  public BillDto createBill(@Valid CreateBillDto dto) {
    groupService.verifyIfGroupIsNotSettledDown(dto.groupId());
    return billService.create(dto);
  }

  @Transactional
  public Collection<BillDto> getBillsByGroupId(String groupId) {
    return billService.getBillsByGroupId(groupId);
  }

  @Transactional
  public Collection<BillDto> getBillsByUserIdAndGroupId(String userId, String groupId) {
    return billService.getBillsByUserIdAndGroupId(userId, groupId);
  }

  @Transactional
  public BillDto updateBill(@Valid CreateBillDto dto, String billId) {
    groupService.verifyIfGroupIsNotSettledDown(dto.groupId());
    return billService.update(billId, dto);
  }

  @Transactional
  public Void deleteBill(String id) {
    groupService.verifyIfGroupIsNotSettledDown(billService.getById(id).groupId());
    return billService.deleteBill(id);
  }

  @Transactional
  public PaymentDto createPayment(@Valid CreatePaymentDto dto) {
    groupService.verifyIfGroupIsNotSettledDown(dto.groupId());
    return billService.createPayment(dto);
  }

  @Transactional
  public PaymentDto getPaymentById(String id) {
    return billService.getPaymentById(id);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsBySenderIdAndGroupId(String senderId, String groupId) {
    return billService.getPaymentsBySenderIdAndGroupId(senderId, groupId);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsByReceiverIdAndGroupId(String receiverId, String groupId) {
    return billService.getPaymentsByReceiverIdAndGroupId(receiverId, groupId);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsByGroupId(String groupId) {
    return billService.getPaymentsByGroupId(groupId);
  }

  @Transactional
  public Collection<PaymentDto> getPaymentsByGroupIdAndSenderIdAndReceiverId(String groupId, String senderId, String receiverId) {
    return billService.getPaymentsByGroupIdAndSenderIdAndReceiverId(groupId, senderId, receiverId);
  }

  @Transactional
  public Void deletePayment(String id) {
    groupService.verifyIfGroupIsNotSettledDown(billService.getPaymentById(id).groupId());
    return billService.deletePayment(id);
  }

  @Transactional
  public List<InvitationDto> createInvitations(@Valid CreateInvitationsDto dto) {
    groupService.verifyIfGroupIsNotSettledDown(dto.groupId());
    return invitationService.create(dto);
  }

  @Transactional
  public InvitationDto getInvitationById(String id) {
    return invitationService.getById(id);
  }

  @Transactional
  public InvitationDto getInvitationByGroupAndInviteeId(String groupId, String inviteeId) {
    return invitationService.getByGroupAndInviteeId(groupId, inviteeId);
  }

  @Transactional
  public Collection<InvitationDto> getInvitationsByGroupId(String groupId, InvitationStatus status) {
    return invitationService.getByGroupId(groupId, status);
  }

  @Transactional
  public Collection<InvitationDto> getInvitationsByInviteeId(String inviteeId, InvitationStatus status) {
    return invitationService.getByInviteeId(inviteeId, status);
  }

  @Transactional
  public void updateInvitationStatus(String id, InvitationStatus invitationStatus, UserDto currentUser) {
    groupService.verifyIfGroupIsNotSettledDown(invitationService.getById(id).groupId());
    invitationService.updateInvitationStatus(id, invitationStatus, currentUser);
  }
}
