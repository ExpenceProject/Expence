import GroupIcon from '@/assets/images/group_icon.svg';
import { GroupIcon as GroupIconSVG } from '@/components/icons/group';
import { Avatar } from '@/components/ui/avatar';
import { Group, GroupMember, GroupWithMembers } from '@/types';
import { openGroupCreationModalAtom } from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { Button, Flex, Heading, Spinner, Text } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { useEffect, useState } from 'react';
import { FaLock, FaUnlock } from 'react-icons/fa';
import { Link } from 'react-router-dom';

import { GroupCreationDialog } from '../group-creation-dialog/group-creation-dialog';

export const UserGroups = () => {
  const [userGroups, setUserGroups] = useState<GroupWithMembers[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [, openGroupCreationModal] = useAtom(openGroupCreationModalAtom);
  const { user } = useUser();

  const handleOpenGroupCreationModal = () => {
    window.scrollTo(0, 0);
    openGroupCreationModal();
  };

  const getUserGroups = async () => {
    return apiClient
      .get(`/groups/user/${user?.id}`)
      .then(async (response) => {
        const groups: Group[] = response.data;

        if (!groups.length) {
          return [];
        }

        const groupsWithMembers: GroupWithMembers[] = await Promise.all(
          groups.map(async (group: Group) => {
            const groupMembers: GroupMember[] = await apiClient
              .get(`/groups/${group.id}/members`)
              .then((response) => response.data)
              .catch((error) => {
                console.error(error);
                return [];
              });
            return {
              ...group,
              members: groupMembers,
            };
          }),
        );

        return groupsWithMembers;
      })
      .catch((error) => {
        console.error(error);
        return [];
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  useEffect(() => {
    getUserGroups()
      .then((groups) => {
        setUserGroups(groups);
      })
      .catch((error) => {
        console.error(error);
      });

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user]);

  return (
    <Flex w="100%" h="100%" maxW="730px" direction="column" gap={5}>
      <Flex w="100%" justify="space-between" align="center">
        <Heading fontFamily="inherit" size="2xl" color="textRaw">
          My Groups
        </Heading>
        <Button
          bg="primary"
          _hover={{ bg: 'hoverPrimary' }}
          transition={'all 0.15s ease'}
          color="textBg"
          onClick={handleOpenGroupCreationModal}
          fontSize={'md'}
        >
          + Create Group
        </Button>
      </Flex>
      {isLoading ? (
        <Spinner w="100px" h="100px" margin="auto" color="primary" />
      ) : userGroups.length > 0 ? (
        userGroups.map((group) => {
          const groupImage =
            group.image.key &&
            `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${group.image.key}`;
          return (
            <Link to={`/groups/${group.id}`} key={group.id}>
              <Flex
                key={group.id}
                gap={4}
                bg="hover"
                _hover={{ bg: 'disabled' }}
                borderRadius={10}
                cursor="pointer"
                transition="all 0.15s ease"
                p={4}
              >
                <Avatar
                  src={group.image.key ? groupImage : GroupIcon}
                  variant="solid"
                  color="textBg"
                  bg="primary"
                  contain="content"
                  w="60px"
                  h="60px"
                  outlineWidth={3}
                  outlineColor="primary"
                  outlineOffset={-1}
                  outlineStyle="solid"
                  p={groupImage ? 0 : 3}
                ></Avatar>
                <Flex direction="column" gap={1}>
                  <Heading fontFamily="inherit" size="lg" color="textRaw">
                    {group.name}
                  </Heading>
                  <Flex gap={3} align="center">
                    {group.settledDown ? (
                      <Flex>
                        <Flex gap={1} align="center">
                          <FaLock style={{ width: '15px', height: '15px' }} />
                          <Text fontSize="md" color="text">
                            Settled
                          </Text>
                        </Flex>
                      </Flex>
                    ) : (
                      <Flex>
                        <Flex gap={1} align="center">
                          <FaUnlock style={{ width: '15px', height: '15px' }} />
                          <Text fontSize="md" color="text">
                            Open
                          </Text>
                        </Flex>
                      </Flex>
                    )}
                    <Flex gap={1} align="center">
                      <GroupIconSVG width={19} height={19} />
                      <Text fontSize="md" color="text">
                        {group.members.length}
                      </Text>
                    </Flex>
                  </Flex>
                </Flex>
              </Flex>
            </Link>
          );
        })
      ) : (
        <Text fontFamily="inherit" fontSize="md" color="text">
          You are not a member of any group yet...
        </Text>
      )}
      <GroupCreationDialog
        getUserGroups={getUserGroups}
        setUserGroups={setUserGroups}
      />
    </Flex>
  );
};
