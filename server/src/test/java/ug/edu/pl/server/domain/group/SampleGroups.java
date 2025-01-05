package ug.edu.pl.server.domain.group;

import ug.edu.pl.server.domain.common.storage.SampleImages;
import ug.edu.pl.server.domain.group.dto.CreateBillDto;
import ug.edu.pl.server.domain.group.dto.CreateGroupDto;

import java.math.BigDecimal;
import java.util.Set;

public final class SampleGroups {
  public static final String ID_THAT_DOES_NOT_EXIST = "1";

  public static final CreateGroupDto VALID_GROUP_NO_FILE_AND_INVITEES =
      new CreateGroupDto("groupName", null, null);
  public static final CreateGroupDto VALID_GROUP_WITH_FILE_AND_NO_INVITEES =
      new CreateGroupDto("groupName2", SampleImages.IMAGE_JPG, Set.of());
  public static final CreateGroupDto INVALID_GROUP = new CreateGroupDto(null, null, Set.of());
  public static final CreateGroupDto INVALID_GROUP_FILE =
      new CreateGroupDto("groupFile", SampleImages.IMAGE_GIF, Set.of());

  public static CreateGroupDto validGroupWithFileAndInvitees(Set<String> invitees) {
    return new CreateGroupDto("groupName3", SampleImages.IMAGE_JPG, invitees);
  }
}
