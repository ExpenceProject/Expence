import GroupIcon from '@/assets/images/group_icon.svg';
import { Avatar } from '@/components/ui/avatar';
import { InvitationWithInviterAndGroup } from '@/types';
import { Button, Flex, MenuItem, Text } from '@chakra-ui/react';
import { FC } from 'react';
import { IoMdCheckmark } from 'react-icons/io';
import { IoCloseOutline } from 'react-icons/io5';

type InvitationProps = {
  invitation: InvitationWithInviterAndGroup;
  inviterImage?: string;
  acceptInvitation: (invitationId: string) => Promise<void>;
  declineInvitation: (invitationId: string) => Promise<void>;
  invitationCreationDate: string | null;
};

export const Invitation: FC<InvitationProps> = ({
  invitation,
  inviterImage,
  acceptInvitation,
  declineInvitation,
  invitationCreationDate,
}) => {
  return (
    <MenuItem
      value="notification"
      p={0}
      bg={'background'}
      transition="all 0.15s ease"
      borderRadius={5}
    >
      <Flex align="center" gap={2} p={3.5}>
        <Flex gap={3} align="center">
          <Avatar
            src={inviterImage || GroupIcon}
            variant="solid"
            color="textBg"
            bg="primary"
            contain="content"
            w={{ base: '40px', md: '55px' }}
            h={{ base: '40px', md: '55px' }}
            outlineWidth={2}
            outlineColor="primary"
            outlineOffset={-1}
            outlineStyle="solid"
            p={inviterImage ? 0 : 3}
          ></Avatar>
          <Flex direction={'column'} gap={1}>
            <Text letterSpacing={0.3} fontSize={{ base: 'xs', md: 'sm' }}>
              {invitation.inviter?.firstName} {invitation.inviter?.lastName}{' '}
              invited you to join <b>{invitation.group?.name}</b>
            </Text>
            <Flex align="flex-end" justify="space-between">
              <Flex gap={1}>
                <Button
                  bg="primary"
                  _hover={{ bg: 'hoverPrimary' }}
                  transition={'all 0.15s ease'}
                  color="textBg"
                  size={'xs'}
                  onClick={() => acceptInvitation(invitation.id)}
                >
                  <IoMdCheckmark />
                </Button>
                <Button
                  bg="backgroundError"
                  _hover={{ bg: 'backgroundErrorHover' }}
                  transition={'all 0.15s ease'}
                  color="textError"
                  size={'xs'}
                  onClick={() => declineInvitation(invitation.id)}
                >
                  <IoCloseOutline />
                </Button>
              </Flex>
              {invitationCreationDate && (
                <Text fontSize="xs" color="textDimmed">
                  {invitationCreationDate}
                </Text>
              )}
            </Flex>
          </Flex>
        </Flex>
      </Flex>
    </MenuItem>
  );
};
