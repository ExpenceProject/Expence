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
import { GroupMemberWithUser } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { Button, Flex, Input, Text } from '@chakra-ui/react';
import { FC, useRef, useState } from 'react';
import { FiEdit } from 'react-icons/fi';
import { IoMdCheckmark } from 'react-icons/io';
import { IoCloseOutline } from 'react-icons/io5';
import { toast } from 'react-toastify';

type MemberProps = {
  member: GroupMemberWithUser;
  isAdmin?: boolean;
  groupId: string;
  getGroupAndMembers: () => void;
};

export const Member: FC<MemberProps> = ({
  member,
  isAdmin,
  groupId,
  getGroupAndMembers,
}) => {
  const [isEditNicknamePopoverOpen, setIsEditNicknamePopoverOpen] =
    useState(false);
  const [isDeleteMemberPopoverOpen, setIsDeleteMemberPopoverOpen] =
    useState(false);
  const [nickname, setNickname] = useState<string | null>(null);

  const inputRef = useRef<HTMLInputElement>(null);

  const memberImage =
    member.userInfo?.image.key &&
    `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${member.userInfo.image.key}`;

  const roleName =
    member.groupRole.name.slice(5).charAt(0) +
    member.groupRole.name.slice(6).toLocaleLowerCase();

  const handleNicknameUpdate = () => {
    if (nickname) {
      if (nickname.length < 2) {
        toast.error('Nickname must be at least 2 characters long');
        setIsEditNicknamePopoverOpen(false);
        return;
      }

      if (nickname.length > 20) {
        toast.error('Nickname must be at most 20 characters long');
        setIsEditNicknamePopoverOpen(false);
        return;
      }

      apiClient
        .patch(`/groups/${groupId}/members/${member.id}/nickname`, nickname, {
          headers: { 'Content-Type': 'application/json' },
        })
        .then(() => {
          getGroupAndMembers();
          toast.success('Nickname updated successfully');
        })
        .catch((error) => {
          console.error(error);
          toast.error(
            'Failed to update nickname, please try again in a few minutes',
          );
        })
        .finally(() => {
          setNickname(null);
          inputRef.current!.value = '';
          setIsEditNicknamePopoverOpen(false);
        });
    } else {
      setIsEditNicknamePopoverOpen(false);
    }
  };

  const handleMemberDelete = () => {
    apiClient
      .delete(`/groups/${groupId}/members/${member.id}`)
      .then(() => {
        getGroupAndMembers();
        toast.info(`Member "${member.nickname}" deleted successfully`);
      })
      .catch((error) => {
        console.error(error);
        toast.error(
          'Failed to delete member, please try again in a few minutes',
        );
      })
      .finally(() => {
        setIsDeleteMemberPopoverOpen(false);
      });
  };

  return (
    <Flex key={member.id} align="center" justify="space-between">
      <Flex align="center" gap={3}>
        <Avatar
          src={memberImage}
          name={`${member.nickname}`}
          variant="solid"
          color="textBg"
          bg="primary"
          contain="content"
          w="40px"
          h="40px"
          outlineWidth={2}
          outlineColor="primary"
          outlineOffset={-1}
          outlineStyle="solid"
          p={member.userInfo?.image.key ? 0 : 3}
        ></Avatar>
        <Flex direction="column">
          <Text
            color="textRaw"
            fontWeight="501"
            fontSize={{ base: 'sm', lg: 'md' }}
          >
            {member.nickname}
          </Text>
          <Text
            fontSize={{ base: 'xs', lg: 'sm' }}
            color={roleName === 'Owner' ? 'highlight' : 'textDimmed'}
          >
            {roleName}
          </Text>
        </Flex>
      </Flex>
      <Flex gap={1} align="center">
        <PopoverRoot
          open={isEditNicknamePopoverOpen}
          onInteractOutside={() => setIsEditNicknamePopoverOpen(false)}
        >
          <PopoverTrigger
            asChild
            onClick={() =>
              setIsEditNicknamePopoverOpen(!isEditNicknamePopoverOpen)
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
              <PopoverTitle>Change nickname</PopoverTitle>
              <Flex gap={2} align="center" pt={2}>
                <Input
                  ref={inputRef}
                  placeholder={member.nickname}
                  _focus={{ border: 'none' }}
                  onChange={(e) => setNickname(e.target.value)}
                />
                <Button
                  color="textBg"
                  _hover={{ bg: 'hoverPrimary' }}
                  bgColor="primary"
                  size={'xs'}
                  style={{ width: '39px', height: '39px' }}
                  p={0}
                  border="none"
                  onClick={handleNicknameUpdate}
                >
                  <IoMdCheckmark
                    style={{ width: '20px', height: '20px', padding: 0 }}
                  />
                </Button>
              </Flex>
            </PopoverBody>
            <PopoverCloseTrigger
              onClick={() => setIsEditNicknamePopoverOpen(false)}
            />
          </PopoverContent>
        </PopoverRoot>
        {isAdmin && (
          <PopoverRoot
            open={isDeleteMemberPopoverOpen}
            onOpenChange={(e) => setIsDeleteMemberPopoverOpen(e.open)}
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
                  Are you sure you want to remove this member from the group?
                </PopoverTitle>
                <Flex gap={4} pt={4}>
                  <Button
                    flex={1}
                    color="textError"
                    _hover={{ bg: 'backgroundErrorHover' }}
                    bgColor="backgroundError"
                    fontSize={{ base: 'sm', md: 'md' }}
                    onClick={handleMemberDelete}
                  >
                    Yes
                  </Button>
                  <Button
                    flex={1}
                    color="textBg"
                    _hover={{ bg: 'hoverPrimary' }}
                    bgColor="primary"
                    fontSize={{ base: 'sm', md: 'md' }}
                    onClick={() => setIsDeleteMemberPopoverOpen(false)}
                  >
                    Cancel
                  </Button>
                </Flex>
              </PopoverBody>
            </PopoverContent>
          </PopoverRoot>
        )}
      </Flex>
    </Flex>
  );
};
