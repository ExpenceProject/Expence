import GroupIcon from '@/assets/images/group_icon.svg';
import { GroupEditImageModal } from '@/components/custom/group-image-dialog/group-image-dialog';
import { PaymentsGroup } from '@/components/custom/payments-group/payments-group';
import { CameraIcon } from '@/components/icons/camera';
import { GroupIcon as GroupIconComponent } from '@/components/icons/group';
import { Avatar } from '@/components/ui/avatar';
import {
  PopoverArrow,
  PopoverBody,
  PopoverCloseTrigger,
  PopoverContent,
  PopoverRoot,
  PopoverTitle,
  PopoverTrigger,
} from '@/components/ui/popover';
import { PageLayout } from '@/layout/page-layout';
import { GroupMember, GroupMemberWithUser, GroupWithMembers } from '@/types';
import { openGroupEditImageModalAtom } from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { useUser } from '@/utils/providers/user-provider/use-user';
import {
  Box,
  Button,
  Flex,
  Heading,
  Input,
  Spinner,
  Text,
} from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { DateTime } from 'luxon';
import { useEffect, useRef, useState } from 'react';
import { FaLock, FaUnlock } from 'react-icons/fa';
import { FaCalendarDays } from 'react-icons/fa6';
import { FiEdit } from 'react-icons/fi';
import { IoMdCheckmark } from 'react-icons/io';
import { IoCloseOutline } from 'react-icons/io5';
import { useParams } from 'react-router';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

import { Member } from '../../components/custom/member/member';

export const GroupPage = () => {
  const navigate = useNavigate();
  const [group, setGroup] = useState<GroupWithMembers | null>(null);
  const [owner, setOwner] = useState<GroupMember | null>(null);
  const [members, setMembers] = useState<GroupMemberWithUser[]>([]);
  const [isAdmin, setIsAdmin] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isEditGroupNamePopoverOpen, setIsEditGroupNamePopoverOpen] =
    useState(false);
  const [groupName, setGroupName] = useState<string | null>(null);
  const { groupId } = useParams<{ groupId: string }>();
  const [isDeleteGroupPopoverOpen, setIsDeleteGroupPopoverOpen] =
    useState(false);
  const { user } = useUser();
  const [isSettleDownPopoverOpen, setIsSettleDownPopoverOpen] = useState(false);

  const [, openGroupEditImageModal] = useAtom(openGroupEditImageModalAtom);

  const inputRef = useRef<HTMLInputElement>(null);

  const headerHeight = import.meta.env.VITE_REACT_HEADER_HEIGHT;

  const groupImage =
    group?.image.key &&
    `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${group.image.key}`;

  const getGroup = async () => {
    return apiClient
      .get(`/groups/${groupId}`)
      .then((response) => response.data)
      .catch((error) => {
        console.error(error);
      });
  };

  const getMembers = async () => {
    return apiClient
      .get(`/groups/${groupId}/members`)
      .then(async (response) => {
        const members: GroupMember[] = response.data;
        if (!members.length) {
          return [];
        }

        const membersWithUsers: GroupMemberWithUser[] = await Promise.all(
          members.map(async (member) => {
            const user = await apiClient
              .get(`/groups/${groupId}/members/${member.id}/user`)
              .then((response) => response.data)
              .catch((error) => {
                console.error(error);
                return null;
              });
            return {
              ...member,
              userInfo: user,
            };
          }),
        );

        return membersWithUsers;
      })
      .catch((error) => {
        console.error(error);
        return [];
      });
  };

  const getGroupAndMembers = () => {
    Promise.all([getGroup(), getMembers()])
      .then(([group, members]) => {
        setGroup(group);

        const owner = members.find(
          (member) =>
            member.groupRole.name ===
            import.meta.env.VITE_REACT_ROLE_OWNER_NAME,
        );

        if (owner) {
          setOwner(owner);
          setMembers(members.filter((member) => member.id !== owner.id));
        }

        if (owner?.user === user?.id) {
          setIsAdmin(true);
        }

        setIsLoading(false);
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const clearGroupNameInput = () => {
    setGroupName(null);
    inputRef.current!.value = '';
    setIsEditGroupNamePopoverOpen(false);
  };

  const handleGroupNameUpdate = () => {
    if (groupName) {
      if (groupName.length < 2) {
        toast.error('Group name must be at least 2 characters long');
        clearGroupNameInput();
        return;
      }

      if (groupName.length > 20) {
        toast.error('Group name must be at most 20 characters long');
        clearGroupNameInput();
        return;
      }

      const groupData = new FormData();
      groupData.append('name', groupName);

      apiClient
        .put(`/groups/${groupId}`, groupData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        })
        .then(() => {
          getGroupAndMembers();
          toast.success('Group name updated successfully');
          inputRef.current!.value = '';
        })
        .catch((error) => {
          console.error(error);
          toast.error(
            'Failed to update group name, please try again in a few minutes',
          );
        })
        .finally(() => {
          clearGroupNameInput();
        });
    } else {
      setIsEditGroupNamePopoverOpen(false);
    }
  };

  const handleGroupDelete = () => {
    apiClient
      .delete(`/groups/${groupId}`)
      .then(() => {
        toast.success('Group deleted successfully');
        navigate('/profile/groups');
      })
      .catch((error) => {
        console.error(error);
        toast.error(
          'Failed to delete group, please try again in a few minutes',
        );
      })
      .finally(() => {
        setIsDeleteGroupPopoverOpen(false);
      });
  };

  const handleSettleDown = () => {
    apiClient
      .patch(`/groups/${groupId}/settledDown`)
      .then(() => {
        toast.success('Group settled down successfully');
        getGroupAndMembers();
      })
      .catch((error) => {
        console.error(error);
        toast.error('Failed to settle down group, please try again later');
      })
      .finally(() => {
        setIsSettleDownPopoverOpen(false);
      });
  };

  useEffect(() => {
    getGroupAndMembers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [groupId, user]);

  if (isLoading) {
    return (
      <Flex h={`calc(100vh - ${headerHeight}px)`} w="100%">
        <Spinner w="150px" h="150px" margin="auto" color="primary" />
      </Flex>
    );
  }

  if (!group || (!members.length && !owner)) {
    return (
      <PageLayout>
        <Text>Something went wrong...</Text>
      </PageLayout>
    );
  }

  return (
    <PageLayout includeGap={false}>
      <Flex gap={5} p={7} borderRadius={10} bg="hover" w="100%" mb={5}>
        <Box
          position="relative"
          w={115}
          h={115}
          display="flex"
          alignItems="center"
          justifyContent="center"
        >
          <Avatar
            src={groupImage || GroupIcon}
            variant="solid"
            color="textBg"
            bg="primary"
            contain="content"
            w="115px"
            h="115px"
            outlineWidth={3}
            outlineColor="primary"
            outlineOffset={-1}
            outlineStyle="solid"
            p={groupImage ? 0 : 4}
            mt={1}
          ></Avatar>
          {isAdmin && (
            <Box
              as="button"
              borderRadius="100%"
              padding={2}
              cursor="pointer"
              _hover={{
                bg: 'disabled',
              }}
              position="absolute"
              bottom={-1.5}
              right={0.5}
              bg="hover"
              outlineWidth={2}
              outlineColor="primary"
              outlineOffset={-1}
              outlineStyle="solid"
              transition="all 0.15s ease"
              onClick={openGroupEditImageModal}
            >
              <CameraIcon width={20} height={20} />
            </Box>
          )}
        </Box>
        <Flex direction="column" gap={{ base: 1.5, lg: 3 }}>
          <Flex align="center" gap={2}>
            <Heading
              color="textRaw"
              fontSize={{ base: 'md', lg: 'xl' }}
              lineHeight={1.3}
            >
              {group.name}
            </Heading>
            {isAdmin && (
              <PopoverRoot
                open={isEditGroupNamePopoverOpen}
                onInteractOutside={() => setIsEditGroupNamePopoverOpen(false)}
              >
                <PopoverTrigger
                  asChild
                  onClick={() =>
                    setIsEditGroupNamePopoverOpen(!isEditGroupNamePopoverOpen)
                  }
                >
                  <Button
                    bg="none"
                    color="text"
                    minW={0}
                    w={8}
                    h={8}
                    _hover={{ bg: 'disabled' }}
                    p={0}
                    mt={-1}
                  >
                    <FiEdit style={{ width: '20px', height: '20px' }} />
                  </Button>
                </PopoverTrigger>
                <PopoverContent>
                  <PopoverArrow position={'top-end'} />
                  <PopoverBody>
                    <PopoverTitle>Change group name</PopoverTitle>
                    <Flex gap={2} align="center" pt={2}>
                      <Input
                        ref={inputRef}
                        placeholder={group.name}
                        _focus={{ border: 'none' }}
                        onChange={(e) => setGroupName(e.target.value)}
                      />
                      <Button
                        color="textBg"
                        _hover={{ bg: 'hoverPrimary' }}
                        bgColor="primary"
                        size={'xs'}
                        style={{ width: '39px', height: '39px' }}
                        p={0}
                        border="none"
                        onClick={handleGroupNameUpdate}
                      >
                        <IoMdCheckmark
                          style={{
                            width: '20px',
                            height: '20px',
                            padding: 0,
                          }}
                        />
                      </Button>
                    </Flex>
                  </PopoverBody>
                  <PopoverCloseTrigger
                    onClick={() => setIsEditGroupNamePopoverOpen(false)}
                  />
                </PopoverContent>
              </PopoverRoot>
            )}
            {isAdmin && (
              <PopoverRoot
                open={isDeleteGroupPopoverOpen}
                onOpenChange={(e) => setIsDeleteGroupPopoverOpen(e.open)}
              >
                <PopoverTrigger asChild>
                  <Button
                    bg="none"
                    color="textError"
                    minW={0}
                    w={8}
                    h={8}
                    _hover={{ bg: 'backgroundErrorHover' }}
                    p={0}
                    mt={-1}
                  >
                    <IoCloseOutline style={{ width: '25px', height: '25px' }} />
                  </Button>
                </PopoverTrigger>
                <PopoverContent>
                  <PopoverArrow />
                  <PopoverBody>
                    <PopoverTitle>
                      Are you sure you want to delete this group?
                    </PopoverTitle>
                    <Flex gap={4} pt={4}>
                      <Button
                        flex={1}
                        color="textError"
                        _hover={{ bg: 'backgroundErrorHover' }}
                        bgColor="backgroundError"
                        fontSize={{ base: 'sm', md: 'md' }}
                        onClick={handleGroupDelete}
                      >
                        Yes
                      </Button>
                      <Button
                        flex={1}
                        color="textBg"
                        _hover={{ bg: 'hoverPrimary' }}
                        bgColor="primary"
                        fontSize={{ base: 'sm', md: 'md' }}
                        onClick={() => setIsDeleteGroupPopoverOpen(false)}
                      >
                        Cancel
                      </Button>
                    </Flex>
                  </PopoverBody>
                </PopoverContent>
              </PopoverRoot>
            )}
            {isAdmin && !group.settledDown && (
              <PopoverRoot
                open={isSettleDownPopoverOpen}
                onOpenChange={(e) => setIsSettleDownPopoverOpen(e.open)}
              >
                <PopoverTrigger asChild>
                  <Button
                    color="textBg"
                    _hover={{ bg: 'hoverPrimary' }}
                    border="none"
                    bg="none"
                    minW={0}
                    size={'xs'}
                    w={8}
                    h={8}
                    p={0}
                    mt={-1}
                  >
                    <FaLock style={{ width: '16px', height: '16px' }} />
                  </Button>
                </PopoverTrigger>
                <PopoverContent>
                  <PopoverArrow />
                  <PopoverBody>
                    <PopoverTitle>
                      Are you sure you want to settle down this group?
                    </PopoverTitle>
                    <Flex gap={4} pt={4}>
                      <Button
                        flex={1}
                        color="textError"
                        _hover={{ bg: 'backgroundErrorHover' }}
                        bgColor="backgroundError"
                        fontSize={{ base: 'sm', md: 'md' }}
                        onClick={handleSettleDown}
                      >
                        Yes
                      </Button>
                      <Button
                        flex={1}
                        color="textBg"
                        _hover={{ bg: 'hoverPrimary' }}
                        bgColor="primary"
                        fontSize={{ base: 'sm', md: 'md' }}
                        onClick={() => setIsSettleDownPopoverOpen(false)}
                      >
                        Cancel
                      </Button>
                    </Flex>
                  </PopoverBody>
                </PopoverContent>
              </PopoverRoot>
            )}
          </Flex>
          <Flex
            align={{ base: 'flex-start', lg: 'center' }}
            direction={{ base: 'column', lg: 'row' }}
            gap={{ base: 2, lg: 5 }}
          >
            <Flex align="center" gap={2}>
              <GroupIconComponent width={19} height={19} />
              <Text fontSize={{ base: 'sm', lg: 'md' }}>
                {members.length + ((owner && 1) || 0) > 1
                  ? `${members.length + ((owner && 1) || 0)} Members`
                  : `${members.length + ((owner && 1) || 0)} Member`}
              </Text>
            </Flex>
            <Flex align="center" gap={2}>
              <FaCalendarDays style={{ width: '17px', height: '17px' }} />
              <Text fontSize={{ base: 'sm', lg: 'md' }}>
                Created{' '}
                {DateTime.fromISO(group.createdAt).toFormat('dd/MM/yyyy')}
              </Text>
            </Flex>
            {group.settledDown ? (
              <Flex gap={2} align="center">
                <FaLock style={{ width: '16px', height: '16px' }} />
                <Text fontSize={{ base: 'sm', lg: 'md' }}>Settled</Text>
              </Flex>
            ) : (
              <Flex gap={2} align="center">
                <FaUnlock style={{ width: '16px', height: '16px' }} />
                <Text fontSize={{ base: 'sm', lg: 'md' }}>Open</Text>
              </Flex>
            )}
          </Flex>
        </Flex>
      </Flex>
      <Flex
        w="100%"
        gap={5}
        direction={{ base: 'column', lg: 'row' }}
        h="min-content"
      >
        <Flex
          gap={5}
          direction="column"
          w={{ base: '100%', lg: '35%' }}
          minW={{ base: '100%', lg: '350px' }}
        >
          <Flex direction="column" gap={5} p={7} borderRadius={10} bg="hover">
            <Text color="textRaw" fontSize={{ base: 'sm', lg: 'md' }}>
              Members
            </Text>
            <Flex direction="column" gap={5}>
              {owner && (
                <Member
                  member={owner}
                  groupId={group.id}
                  getGroupAndMembers={getGroupAndMembers}
                />
              )}
              {members.map((member) => (
                <Member
                  key={member.id}
                  member={member}
                  isAdmin={isAdmin}
                  groupId={group.id}
                  getGroupAndMembers={getGroupAndMembers}
                />
              ))}
            </Flex>
          </Flex>
        </Flex>
        <Flex
          direction="column"
          gap={5}
          p={7}
          borderRadius={10}
          bg="hover"
          w={{ base: '100%', lg: '65%' }}
        >
          <PaymentsGroup groupId={groupId} />
        </Flex>
      </Flex>
      <GroupEditImageModal
        group={group}
        getGroupAndMembers={getGroupAndMembers}
      />
    </PageLayout>
  );
};
