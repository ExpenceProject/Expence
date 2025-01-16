import { useColorModeValue } from '@/components/ui/color-mode';
import { GroupMemberWithUser, User } from '@/types';
import {
  closeMembershipInvitationModalAtom,
  isMembershipInvitationModalOpenAtom,
} from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { useUser } from '@/utils/providers/user-provider/use-user';
import {
  Button,
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  Text,
} from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { FC, useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { MultiSelect, Option } from 'react-multi-select-component';
import { toast } from 'react-toastify';
import styled from 'styled-components';

type MembershipInvitationCreationDialogProps = {
  currentMembers: GroupMemberWithUser[];
  groupId: string;
  inviterId: string | undefined;
};

const StyledForm = styled('form')`
  width: 100%;
`;

export const MembershipInvitationCreationDialog: FC<
  MembershipInvitationCreationDialogProps
> = ({ currentMembers, groupId, inviterId }) => {
  const [isMembershipInvitationModalOpen] = useAtom(
    isMembershipInvitationModalOpenAtom,
  );
  const [, closeMembershipInvitationModal] = useAtom(
    closeMembershipInvitationModalAtom,
  );
  const [isLoading, setIsLoading] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<Option[]>([]);

  const { user } = useUser();

  const usersOptions = useMemo(() => {
    return users
      .filter((userOption) => userOption.id !== user?.id)
      .map((user) => {
        return { label: `${user.firstName} ${user.lastName}`, value: user.id };
      });
  }, [user, users]);

  const currentTheme = useColorModeValue('light', 'dark');

  const onSubmit = () => {
    setIsLoading(true);
    const groupData = new FormData();

    groupData.append('groupId', groupId);
    groupData.append('inviterId', inviterId || '');

    if (selectedUsers.length > 0) {
      groupData.append(
        'inviteeIds',
        selectedUsers.map((user) => user.value).join(','),
      );
    }

    apiClient
      .post('/invitations', groupData)
      .then(() => {
        closeMembershipInvitationModal();
        toast.success('User invited successfully');
      })
      .catch((error) => {
        console.error(error);
        toast.error('Failed to invite user, please try again later!');
      })
      .finally(() => {
        reset();
        setSelectedUsers([]);
        setIsLoading(false);
      });
  };

  const { handleSubmit, reset } = useForm<{ name: string }>();

  useEffect(() => {
    apiClient
      .get('/users')
      .then((response) => {
        const filteredUsers = response.data.filter((user: User) =>
          currentMembers.every((member) => member.user !== user.id),
        );
        setUsers(filteredUsers);
      })
      .catch((error) => {
        console.error(error);
      });
  }, [currentMembers]);

  useEffect(() => {}, []);

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isMembershipInvitationModalOpen}
      onInteractOutside={closeMembershipInvitationModal}
      onEscapeKeyDown={closeMembershipInvitationModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogContent
        shadow="xl"
        p={4}
        css={{
          position: 'absolute',
          left: '50%',
          transform: 'translateX(-50%)',
        }}
      >
        <DialogCloseTrigger
          _icon={{ w: 6, h: 6 }}
          onClick={closeMembershipInvitationModal}
        />
        <DialogHeader p={4}>
          <DialogTitle fontSize="xl">Invite members</DialogTitle>
        </DialogHeader>
        <DialogBody
          data-theme={currentTheme}
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexDirection="column"
          p={{ base: 2, md: 8 }}
        >
          <StyledForm onSubmit={handleSubmit(onSubmit)}>
            <Text fontSize="sm" color="text">
              Members
            </Text>
            <MultiSelect
              options={usersOptions}
              value={selectedUsers}
              onChange={setSelectedUsers}
              labelledBy="Select"
              className="multi-select"
              hasSelectAll={false}
              valueRenderer={(selected) => {
                return selected.length > 0
                  ? selected.map((option) => option.label).join(', ')
                  : 'Select members...';
              }}
              overrideStrings={{
                selectSomeItems: 'Select members...',
                allItemsAreSelected: 'All members are selected',
                search: 'Search',
                noOptions: 'No members found',
              }}
            />
            <Button
              fontSize="lg"
              bg="primary"
              color="textBg"
              _hover={{ bg: 'hover' }}
              disabled={isLoading}
              mt={8}
              type="submit"
            >
              Submit
            </Button>
          </StyledForm>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
};
